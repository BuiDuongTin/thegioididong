package com.hutech.buiduongtin.service;

import com.hutech.buiduongtin.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    void save(User user);

    List<User> findAll();

    User findByUsername(String username);
}
