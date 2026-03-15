package bg.sofia.uni.fmi.mjt.exceptions;

import bg.sofia.uni.fmi.mjt.exceptions.api.ApiKeyLimitExceededException;
import bg.sofia.uni.fmi.mjt.exceptions.api.BadRequestException;
import bg.sofia.uni.fmi.mjt.exceptions.api.DataUnavailableException;
import bg.sofia.uni.fmi.mjt.exceptions.api.InsufficientPermissions;
import bg.sofia.uni.fmi.mjt.exceptions.api.UnauthorizedKeyException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class APIExceptionPropagate {
    public static <T> T joinOrPropagate(CompletableFuture<T> future)
        throws ApiKeyLimitExceededException, DataUnavailableException, BadRequestException, InsufficientPermissions,
        UnauthorizedKeyException {
        try {
            return future.join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            String message = cause.getMessage();
            switch (cause) {
                case ApiKeyLimitExceededException apiKeyLimitExceededException ->
                    throw new ApiKeyLimitExceededException(message);
                case BadRequestException badRequestException -> throw new BadRequestException(message);
                case DataUnavailableException dataUnavailableException -> throw new DataUnavailableException(message);
                case InsufficientPermissions insufficientPermissions -> throw new InsufficientPermissions(message);
                case UnauthorizedKeyException unauthorizedKeyException -> throw new UnauthorizedKeyException(message);
                default -> {
                }
            }
        }
        return null;
    }
}
