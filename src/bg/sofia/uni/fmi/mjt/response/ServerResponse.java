package bg.sofia.uni.fmi.mjt.response;

import bg.sofia.uni.fmi.mjt.entity.User;

public record ServerResponse(String status, String message, User user) {

}

