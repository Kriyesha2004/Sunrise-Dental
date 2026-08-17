package com.clinic.dao;

import com.clinic.model.User;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    User save(User user);
}
