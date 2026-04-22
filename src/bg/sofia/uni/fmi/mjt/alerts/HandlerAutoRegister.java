package bg.sofia.uni.fmi.mjt.alerts;

import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

public class HandlerAutoRegister {

    public static void registerHandlers(EventDispatcher dispatcher, String packageName) {
        Reflections reflections = new Reflections(packageName);

        Set<Class<?>> handlerClasses = reflections.getTypesAnnotatedWith(EventHandlerClass.class);

        for (Class<?> handlerClass : handlerClasses) {
            try {
                Object instance = handlerClass.getDeclaredConstructor().newInstance();

                for (Method method : handlerClass.getDeclaredMethods()) {
                    if (method.getName().startsWith("on") && method.getParameterCount() == 1) {
                        Class<?> eventType = method.getParameterTypes()[0];
                        registerEvent(dispatcher, method, (Class<? extends Event>) eventType, instance);
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void registerEvent(EventDispatcher dispatcher, Method method,
                                      Class<? extends Event> eventType, Object instance) {
        dispatcher.registerHandler(
                eventType,
                event -> {
                    try {
                        method.invoke(instance, event);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
