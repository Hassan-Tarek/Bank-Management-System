package com.bms.service.impl;

import com.bms.dto.UserDto;
import com.bms.entity.User;
import com.bms.enums.Role;
import com.bms.exception.BadRequestException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.mapper.UserMapper;
import com.bms.repository.UserRepository;
import com.bms.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsersByFilters(Role role) {
        return userRepository.findByFilters(role)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto upgradeUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("User is already an admin!");
        }

        user.setRole(Role.ADMIN);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
