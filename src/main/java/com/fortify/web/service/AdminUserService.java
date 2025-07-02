package com.fortify.web.service;

import com.fortify.web.domain.User;
import com.fortify.web.dto.UserEditDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdminUserService {
    Page<User> findAllUsers(Pageable pageable, String search);
    Optional<User> findUserById(Long id);
    User saveUser(UserEditDto userEditDto);
    void deleteUser(Long id);
    User toggleUserStatus(Long id);
}
