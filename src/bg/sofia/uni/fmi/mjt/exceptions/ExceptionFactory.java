package bg.sofia.uni.fmi.mjt.exceptions;

import bg.sofia.uni.fmi.mjt.entity.ErrorResponse;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiKeyLimitExceededException;
import bg.sofia.uni.fmi.mjt.exceptions.api.BadRequestException;
import bg.sofia.uni.fmi.mjt.exceptions.api.DataUnavailableException;
import bg.sofia.uni.fmi.mjt.exceptions.api.InsufficientPermissions;
import bg.sofia.uni.fmi.mjt.exceptions.api.UnauthorizedKeyException;
import com.google.gson.Gson;

public final class ExceptionFactory {
    private static final int BAD_REQUEST_STATUS_CODE = 400;
    private static final int UNAUTHORIZED_KEY_STATUS_CODE = 401;
    private static final int INSUFFICIENT_PERMISSIONS_STATUS_CODE = 403;
    private static final int EXCEEDED_API_KEY_LIMIT_STATUS_CODE = 429;
    private static final int SERVER_ERROR_STATUS_CODE = 550;
    private static final String EXCEPTION_MESSAGE_PREFIX = "Cause: ";
    private static final Gson GSON = new Gson();

    public static Throwable of(int statusCode, String response) {
        return switch (statusCode) {
            case BAD_REQUEST_STATUS_CODE -> new BadRequestException(extractMessage(response));
            case UNAUTHORIZED_KEY_STATUS_CODE -> new UnauthorizedKeyException(extractMessage(response));
            case INSUFFICIENT_PERMISSIONS_STATUS_CODE -> new InsufficientPermissions(extractMessage(response));
            case EXCEEDED_API_KEY_LIMIT_STATUS_CODE -> new ApiKeyLimitExceededException(extractMessage(response));
            case SERVER_ERROR_STATUS_CODE -> new DataUnavailableException(extractMessage(response));
            default -> null;
        };
    }

    private static String extractMessage(String response) {
        return EXCEPTION_MESSAGE_PREFIX + GSON.fromJson(response, ErrorResponse.class).error();
    }

}

