package com.alpengotter.dodo_project.domain.mapper;


import com.alpengotter.dodo_project.domain.dto.UserBaseDto;
import com.alpengotter.dodo_project.domain.dto.UserExcelDto;
import com.alpengotter.dodo_project.domain.dto.UserResponseDto;
import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.domain.mapper.service.UserMapperService;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = UserMapperService.class)
public interface UserMapper {
    @Mapping(target = "clinics", source = "entity.userClinicMap", qualifiedByName = "mapUserClinicMap")
    UserResponseDto toUserResponseDto(UserEntity entity);
    List<UserResponseDto> toListUserResponseDto(Page<UserEntity> entities);
    List<UserResponseDto> toListUserResponseDto(List<UserEntity> entities);
    @Mapping(target = "isActive", source = "userBaseDto.isActive", defaultValue = "true")
    UserEntity toUserEntity(UserBaseDto userBaseDto);

    @Mapping(target = "name", source = "userEntity", qualifiedByName = "mapFullName")
    @Mapping(target = "clinic", source = "userEntity", qualifiedByName = "mapClinics")
    @Mapping(target = "countLemons", source = "userEntity.lemons")
//    @Mapping(target = "countDiamonds", source = "userEntity.diamonds")
    UserExcelDto toUserExcelDto(UserEntity userEntity);

    List<UserExcelDto> toUserExcelDtoList (List<UserEntity> userEntityList);

    @Mapping(target = "id", source = "id")
    void updateUser(UserBaseDto source, @MappingTarget UserEntity target, Integer id);

}
