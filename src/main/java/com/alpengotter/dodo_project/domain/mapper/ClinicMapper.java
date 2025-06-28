package com.alpengotter.dodo_project.domain.mapper;


import com.alpengotter.dodo_project.domain.dto.ClinicBaseDto;
import com.alpengotter.dodo_project.domain.dto.ClinicResponseDto;
import com.alpengotter.dodo_project.domain.entity.ClinicEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClinicMapper {
    ClinicResponseDto toClinicResponseDto(ClinicEntity entity);
//    List<UserResponseDto> toListUserResponseDto(Page<UserEntity> entities);
    List<ClinicResponseDto> toClinicResponseDtoList(Page<ClinicEntity> entities);
//    @Mapping(target = "isActive", source = "userBaseDto.isActive", defaultValue = "true")
    ClinicEntity toClinicEntity(ClinicBaseDto clinicBaseDto);
//
//    @Mapping(target = "name", source = "userEntity", qualifiedByName = "mapFullName")
//    @Mapping(target = "countLemons", source = "userEntity.lemons")
//    @Mapping(target = "countDiamonds", source = "userEntity.diamonds")
//    UserExcelDto toUserExcelDto(UserEntity userEntity);
//
//    List<UserExcelDto> toUserExcelDtoList (List<UserEntity> userEntityList);

}
