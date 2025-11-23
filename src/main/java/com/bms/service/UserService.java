package com.bms.service;

import com.bms.dto.request.UserUpdateRequestDto;
import com.bms.dto.response.UserResponseDto;
import com.bms.entity.User;
import com.bms.enums.Role;
import com.bms.exception.BadRequestException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.mapper.UserMapper;
import com.bms.repository.AccountRepository;
import com.bms.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Role role, Boolean active, Pageable pageable) {
        return userRepository.findAllUsers(role, active, pageable)
                .map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponseDto updateProfile(UserUpdateRequestDto userUpdateRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Apply updates
        userMapper.partialUpdate(userUpdateRequestDto, user);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public void deactivateProfile(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found."));

        if (!customer.getActive()) {
            throw new BadRequestException("Customer is already inactive.");
        }

        accountRepository.findByCustomerId(customerId)
                .forEach(account -> accountService.closeAccount(account.getId(), customerId));
        customer.setActive(false);
        userRepository.save(customer);
    }
}
