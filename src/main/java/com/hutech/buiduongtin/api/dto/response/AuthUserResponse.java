package com.hutech.buiduongtin.api.dto.response;

import java.util.Set;

public record AuthUserResponse(
        Long id,
        String username,
        String email,
        String phone,
        Set<String> roles) {
}
