package com.hutech.buiduongtin.config;

import com.hutech.buiduongtin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final UserService userService;
        private final PasswordEncoder passwordEncoder;

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                var auth = new DaoAuthenticationProvider();
                auth.setUserDetailsService(userService);
                auth.setPasswordEncoder(passwordEncoder);
                return auth;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/v1/**"))
                                .authorizeHttpRequests(auth -> auth
                                                // Công khai: tài nguyên tĩnh + đăng nhập/đăng ký + danh sách sản phẩm
                                                .requestMatchers(
                                                                "/css/**", "/js/**", "/images/**", "/fonts/**",
                                                                "/", "/home", "/login", "/register", "/error", "/access-denied",
                                                                "/swagger-ui/**", "/v3/api-docs/**")
                                                .permitAll()
                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/system/ping", "/api/v1/products", "/api/v1/products/**",
                                                                "/api/v1/categories", "/api/v1/categories/**")
                                                .permitAll()
                                                .requestMatchers("/products").permitAll()
                                                // USER/ADMIN/MANAGER: chỉ được xem chi tiết sản phẩm
                                                .requestMatchers("/products/detail/**")
                                                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER")
                                                // ADMIN/MANAGER: được thêm/sửa/xóa sản phẩm
                                                .requestMatchers("/products/add", "/products/edit/**", "/products/delete/**",
                                                                "/products/update/**")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                                                // Chỉ USER được vào trang điểm và sử dụng điểm trong luồng mua hàng
                                                .requestMatchers("/user/points", "/user/redeem", "/user/verify-otp", "/cart/**", "/order/**")
                                                .hasAuthority("ROLE_USER")
                                                // Voucher validation endpoints (user only)
                                                .requestMatchers("/voucher/**").hasAuthority("ROLE_USER")
                                                // Các URL sản phẩm khác chỉ dành cho ADMIN
                                                .requestMatchers("/products/**").hasAuthority("ROLE_ADMIN")
                                                // Chỉ ADMIN mới được vào khu vực admin
                                                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                                                // Các trang còn lại: chỉ ADMIN (đảm bảo MANAGER bị giới hạn ngoài sản phẩm)
                                                .anyRequest().hasAuthority("ROLE_ADMIN"))
                                 .formLogin(form -> form
                                                 .loginPage("/login")
                                                 .loginProcessingUrl("/login")
                                                 .defaultSuccessUrl("/products", true)
                                                 .failureUrl("/login?error=true")
                                                 .permitAll())
                                 .logout(logout -> logout
                                                 .logoutUrl("/logout")
                                                 .logoutSuccessUrl("/login?logout=true")
                                                 .invalidateHttpSession(true)
                                                 .deleteCookies("JSESSIONID")
                                                 .permitAll())
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/access-denied"))

                                .authenticationProvider(authenticationProvider())
                                .build();
        }
}
