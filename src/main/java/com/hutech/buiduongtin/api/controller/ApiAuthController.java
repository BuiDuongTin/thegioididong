package com.hutech.buiduongtin.api.controller;

import com.hutech.buiduongtin.api.dto.request.LoginRequest;
import com.hutech.buiduongtin.api.dto.request.RegisterRequest;
import com.hutech.buiduongtin.api.dto.response.ApiResponse;
import com.hutech.buiduongtin.api.dto.response.AuthResponse;
import com.hutech.buiduongtin.api.mapper.AuthUserMapper;
import com.hutech.buiduongtin.model.User;
import com.hutech.buiduongtin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        if (userService.findByUsername(request.username()) != null) {
            throw new IllegalStateException("Tên đăng nhập đã tồn tại");
        }
        if (request.email() != null && !request.email().isBlank() && userService.existsByEmail(request.email())) {
            throw new IllegalStateException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.username().trim());
        user.setPassword(request.password());
        user.setEmail(request.email().trim());
        user.setPhone(request.phone());
        userService.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username().trim(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpRequest.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        User savedUser = userService.findByUsername(request.username().trim());
        return ApiResponse.success("Đăng ký thành công", new AuthResponse(true, "SESSION", AuthUserMapper.toResponse(savedUser)));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username().trim(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpRequest.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        User user = userService.findByUsername(request.username().trim());
        return ApiResponse.success("Đăng nhập thành công", new AuthResponse(true, "SESSION", AuthUserMapper.toResponse(user)));
    }
}
