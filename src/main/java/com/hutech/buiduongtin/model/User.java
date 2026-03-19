package com.hutech.buiduongtin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    @NotBlank(message = "Vui lòng nhập tên đăng nhập")
    private String username;

    @Column(length = 250, nullable = false)
    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;

    @Column(length = 50, nullable = false, unique = true)
    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Column(length = 20)
    private String phone;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
