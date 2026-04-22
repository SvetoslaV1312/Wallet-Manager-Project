package bg.sofia.uni.fmi.mjt.alerts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventDispatcher {
    private Map<Class<? extends Event>, List<Consumer<? extends Event>>> handlers = new HashMap<>();
    //The list permits handlers.get(CryptoPriceUpdateEvent.class)
    //    → [cache::update, notifier::send, logger::log, alertService::check]
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public <E extends Event> void registerHandler(Class<E> eventType, Consumer<E> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    //cast
    public <E extends Event> void dispatch(E event) {
        List<Consumer<? extends Event>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            eventHandlers.forEach(handler ->
                    executor.submit(() ->((Consumer<E>) handler).accept(event)));
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
