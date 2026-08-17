package com.clinic.service;

import com.clinic.model.User;
import java.util.Optional;

public interface AuthService {
    Optional<User> login(String username, String password);
    Optional<User> getUserById(Long id);
}
