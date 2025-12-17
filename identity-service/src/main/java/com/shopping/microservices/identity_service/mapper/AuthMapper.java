package com.shopping.microservices.identity_service.mapper;

import com.shopping.microservices.identity_service.dto.LoginResponseDTO;
import com.shopping.microservices.identity_service.dto.RegistrationDTO;
import com.shopping.microservices.identity_service.entity.SignUpEntity;
import com.shopping.microservices.identity_service.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = {RoleMapper.class})
public interface AuthMapper {

    SignUpEntity map(RegistrationDTO registrationDTO, @MappingTarget SignUpEntity signUpEntity);

    LoginResponseDTO map(UserEntity user);
}
