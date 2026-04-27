package com.hutech.buiduongtin.api.dto.response;

public record AuthResponse(
        boolean authenticated,
        String sessionType,
        AuthUserResponse user) {
}
