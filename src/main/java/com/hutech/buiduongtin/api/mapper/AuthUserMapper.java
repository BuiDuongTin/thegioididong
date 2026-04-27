package com.hutech.buiduongtin.api.mapper;

import com.hutech.buiduongtin.api.dto.response.AuthUserResponse;
import com.hutech.buiduongtin.model.Role;
import com.hutech.buiduongtin.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public final class AuthUserMapper {

    private AuthUserMapper() {
    }

    public static AuthUserResponse toResponse(User user) {
        Set<String> roles = user.getRoles() == null ? Set.of() : user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), roles);
    }
}
