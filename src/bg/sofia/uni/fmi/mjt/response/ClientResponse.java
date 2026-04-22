package bg.sofia.uni.fmi.mjt.response;

import bg.sofia.uni.fmi.mjt.entity.User;

public record ClientResponse(String command, User user) {
}
