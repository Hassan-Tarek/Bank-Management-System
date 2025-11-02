package com.bms.service;

import com.bms.dto.UserDto;
import com.bms.enums.Role;

import java.util.List;

public interface UserService {

    List<UserDto> getUsersByFilters(Role role);

    UserDto getUserById(Long userId);

    UserDto upgradeUser(Long userId);
}
