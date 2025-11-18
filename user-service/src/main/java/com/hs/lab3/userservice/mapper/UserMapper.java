package com.hs.lab3.userservice.mapper;

import com.hs.lab3.userservice.dto.UserDto;
import com.hs.lab3.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDto toUserDto(User user);
    List<UserDto> toUserDtoList(List<User> users);
}
