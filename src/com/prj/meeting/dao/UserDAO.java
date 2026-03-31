package com.prj.meeting.dao;

import com.prj.meeting.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findById(int id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    boolean save(User user);
    boolean update(User user);
    boolean delete(int id);
    List<User> findByRole(String role);
}
