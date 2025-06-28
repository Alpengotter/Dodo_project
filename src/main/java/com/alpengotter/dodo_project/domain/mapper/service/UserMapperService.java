package com.alpengotter.dodo_project.domain.mapper.service;

import com.alpengotter.dodo_project.domain.entity.ClinicEntity;
import com.alpengotter.dodo_project.domain.entity.UserClinicMapEntity;
import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.domain.repository.ClinicRepository;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapperService {
    private final ClinicRepository clinicRepository;

    @Named("mapFullName")
    public String mapFullName(UserEntity userEntity) {
        StringBuilder result = new StringBuilder();
        if (Objects.nonNull(userEntity.getLastName())) {
            result.append(userEntity.getLastName());
            result.append(" ");
        }
        if (Objects.nonNull(userEntity.getFirstName())) {
            result.append(userEntity.getFirstName());
        }
        return result.toString();
    }

    @Named("mapClinics")
    public String mapClinics(UserEntity userEntity) {
        StringBuilder result = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        List<String> clinics = userEntity.getUserClinicMap().stream()
            .map(UserClinicMapEntity::getClinic)
            .map(ClinicEntity::getName)
            .collect(Collectors.toList());
        for (int i = 0; i < clinics.size(); i++) {
            result.append(clinics.get(i));
            if (i != clinics.size() - 1) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    @Named("mapUserClinicMap")
    public List<String> mapUserClinicMap(Set<UserClinicMapEntity> userClinicMap) {
        return userClinicMap.stream()
            .map(UserClinicMapEntity::getClinic)
            .map(ClinicEntity::getName)
            .collect(Collectors.toList());
    }

}
