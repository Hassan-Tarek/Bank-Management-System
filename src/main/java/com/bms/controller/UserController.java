package com.bms.controller;

import com.bms.dto.request.UserUpdateRequestDto;
import com.bms.dto.response.UserResponseDto;
import com.bms.enums.Role;
import com.bms.security.CustomUserDetails;
import com.bms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/users",
        produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/all")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserResponseDto> users = userService.getAllUsers(role, active, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long userId) {
        UserResponseDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getId();
        UserResponseDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping(path = "/me/update")
    public ResponseEntity<UserResponseDto> updateMyProfile(
            @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        UserResponseDto user = userService.updateProfile(userUpdateRequestDto, userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping(path = "/me/deactivate")
    public ResponseEntity<Void> deactivateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getUser().getId();
        userService.deactivateProfile(customerId);
        return ResponseEntity.noContent().build();
    }
}
