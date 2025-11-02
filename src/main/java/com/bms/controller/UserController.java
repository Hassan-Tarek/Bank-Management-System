package com.bms.controller;

import com.bms.dto.UserDto;
import com.bms.enums.Role;
import com.bms.security.CustomUserDetails;
import com.bms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users",
        produces = "application/json")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Iterable<UserDto>> getUserByFilters(
            @RequestParam(required = false) Role role) {
        List<UserDto> users = userService.getUsersByFilters(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getId();
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping(path = "/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> upgradeUser(@PathVariable Long userId) {
        UserDto user = userService.upgradeUser(userId);
        return ResponseEntity.ok(user);
    }
}
