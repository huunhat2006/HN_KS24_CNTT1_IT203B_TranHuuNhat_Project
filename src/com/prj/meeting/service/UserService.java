package com.prj.meeting.service;

import com.prj.meeting.dao.UserDAO;
import com.prj.meeting.dao.impl.UserDAOImpl;
import com.prj.meeting.model.User;
import com.prj.meeting.util.PasswordHash;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAOImpl();
    }
    
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordHash.verifyPassword(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    
    public boolean registerUser(String username, String password, String role, String fullName, 
                               String department, String phoneNumber, String email) {
        if (userDAO.findByUsername(username).isPresent()) {
            return false;
        }
        
        String hashedPassword = PasswordHash.hashPassword(password);
        User user = new User(username, hashedPassword, role, fullName, department, phoneNumber, email);
        return userDAO.save(user);
    }
    
    public Optional<User> getUserById(int userId) {
        return userDAO.findById(userId);
    }
    
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    public List<User> getUsersByRole(String role) {
        return userDAO.findByRole(role);
    }
    
    public boolean updateUser(User user) {
        return userDAO.update(user);
    }
    
    public boolean deleteUser(int userId) {
        return userDAO.delete(userId);
    }
    
    public boolean createUser(User user) {
        if (userDAO.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }
        String hashedPassword = PasswordHash.hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        return userDAO.save(user);
    }
    
    public boolean isUsernameExists(String username) {
        return userDAO.findByUsername(username).isPresent();
    }
}
