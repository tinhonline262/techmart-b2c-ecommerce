package com.shopping.microservices.identity_service.mapper;

import com.shopping.microservices.identity_service.entity.SignUpEntity;
import com.shopping.microservices.identity_service.mapper.RoleMapper;
import com.shopping.microservices.identity_service.dto.UserProfileDTO;
import com.shopping.microservices.identity_service.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(uses = {RoleMapper.class})
public interface UserMapper {

    UserProfileDTO mapToProfileDTO(UserEntity entity);
    UserEntity mapToEntity(SignUpEntity signUpEntity);
}
