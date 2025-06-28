package com.alpengotter.dodo_project.service;

import com.alpengotter.dodo_project.domain.dto.ClinicBaseDto;
import com.alpengotter.dodo_project.domain.dto.ClinicCurrencyUpdateDto;
import com.alpengotter.dodo_project.domain.dto.ClinicResponseDto;
import com.alpengotter.dodo_project.domain.dto.StatResponseDto;
import com.alpengotter.dodo_project.domain.dto.UserCurrencyMultipleUpdateDto;
import com.alpengotter.dodo_project.domain.dto.UserCurrencyUpdateDto;
import com.alpengotter.dodo_project.domain.dto.UserResponseDto;
import com.alpengotter.dodo_project.domain.dto.UserStatusMultipleUpdateDto;
import com.alpengotter.dodo_project.domain.dto.UserStatusUpdateDto;
import com.alpengotter.dodo_project.domain.entity.ClinicEntity;
import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.domain.enums.AnalitiqueType;
import com.alpengotter.dodo_project.domain.mapper.ClinicMapper;
import com.alpengotter.dodo_project.domain.mapper.UserMapper;
import com.alpengotter.dodo_project.domain.repository.ClinicRepository;
import com.alpengotter.dodo_project.domain.repository.UserRepository;
import com.alpengotter.dodo_project.handler.ErrorType;
import com.alpengotter.dodo_project.handler.exception.LemonBankException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO Сделать общий метод с помощью ссылки на метод (Functional<T>)
@Service
@Slf4j
@RequiredArgsConstructor
public class ClinicService {

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final UserMapper userMapper;
    private final ClinicMapper clinicMapper;
    private final HistoryService historyService;
    private final AnalitiqueService analitiqueService;

    @Transactional
    public List<UserResponseDto> getAllUsers(Integer offset, Integer limit) {
        Page<UserEntity> users = userRepository.findAllAndIsActiveIsTrue(PageRequest.of(offset, limit));
        return userMapper.toListUserResponseDto(users);
    }

    @Transactional
    public ClinicResponseDto getUserById(Integer id) {
        Optional<ClinicEntity> clinic = clinicRepository.findById(id);
        if (clinic.isEmpty()) {
            throw new LemonBankException(ErrorType.CLINIC_NOT_FOUND);
        }
        ClinicEntity clinicEntity = clinic.get();
        return clinicMapper.toClinicResponseDto(clinicEntity);
    }

    @Transactional
    public UserResponseDto getUserByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmailContainingIgnoreCaseAndIsActiveIsTrue(email);
        if (user.isEmpty()) {
            throw new LemonBankException(ErrorType.USER_NOT_FOUND);
        }
        UserEntity userEntity = user.get();
        return userMapper.toUserResponseDto(userEntity);
    }

    @Transactional
    public UserResponseDto updateEmployeeCurrency(Integer id,
        UserCurrencyUpdateDto currencyUpdateDtoDto) {
        Optional<UserEntity> user = userRepository.findByIdAndIsActiveIsTrue(id);
        if (user.isEmpty()) {
            throw new LemonBankException(ErrorType.USER_NOT_FOUND);
        }
        UserEntity userEntity = user.get();
        Integer currentLemons = userEntity.getLemons();
        Integer currentDiamonds = userEntity.getDiamonds();
        Integer differenceLemons = currencyUpdateDtoDto.getLemons() - currentLemons;
        Integer differenceDiamonds = currencyUpdateDtoDto.getDiamonds() - currentDiamonds;

        userEntity.setDiamonds(currencyUpdateDtoDto.getDiamonds());
        userEntity.setLemons(currencyUpdateDtoDto.getLemons());

        UserEntity saved = userRepository.save(userEntity);

        historyService.changeCurrency(saved, differenceLemons, differenceDiamonds,
            currencyUpdateDtoDto.getComment());

        String currency;
        if (differenceDiamonds != 0) {
            currency = "diamonds";
            analitiqueService.saveAnalitique(AnalitiqueType.REWARD.getMessage(), differenceDiamonds, currency);
        } else if (differenceLemons != 0) {
            currency = "lemons";
            analitiqueService.saveAnalitique(AnalitiqueType.REWARD.getMessage(), differenceLemons, currency);
        }

        return userMapper.toUserResponseDto(saved);
    }

    private boolean isEnglishSymbols(String value) {
        return value.matches("^[a-zA-Z0-9.@]+$");
    }

    @Transactional
    public UserResponseDto updateEmployeeStatus(Integer id, UserStatusUpdateDto statusUpdateDto) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new LemonBankException(ErrorType.USER_NOT_FOUND);
        }
        UserEntity userEntity = user.get();
        userEntity.setIsActive(statusUpdateDto.getIsActive());
        if (!statusUpdateDto.getIsActive()) {
            analitiqueService.saveAnalitique(AnalitiqueType.DEACTIVATE.getMessage(), null, null);
        }
        UserEntity saved = userRepository.save(userEntity);

        return userMapper.toUserResponseDto(saved);
    }

    @Transactional
    public List<Integer> updateCurrencyForMultipleUsers(UserCurrencyMultipleUpdateDto updateDto) {
        List<Integer> userIds = updateDto.getUserIds();
        Integer count = updateDto.getCount();
        List<UserEntity> users = userRepository.findAllByIdInAndIsActiveIsTrue(userIds);
        if (updateDto.getCurrency().equals("lemons")) {
            users
                .forEach(user -> {
                    Integer currentLemons = user.getLemons();
                    user.setLemons(currentLemons + count);
                    UserEntity saved = userRepository.save(user);
                    historyService.changeCurrency(saved, count, 0, updateDto.getComment());
                });

        } else if (updateDto.getCurrency().equals("diamonds")) {
            users
                .forEach(user -> {
                    Integer currentDiamonds = user.getDiamonds();
                    user.setDiamonds(currentDiamonds + count);
                    UserEntity saved = userRepository.save(user);
                    historyService.changeCurrency(saved, 0, count, updateDto.getComment());
                });
        } else {
            throw new LemonBankException(ErrorType.NOT_CORRECT_CURRENCY);
        }
        return userIds;
    }

    @Transactional
    public List<Integer> updateStatusForMultipleUsers(UserStatusMultipleUpdateDto updateDto) {
        List<Integer> userIds = updateDto.getUserIds();
        boolean isActive = updateDto.getIsActive();
        userRepository.updateIsActiveForIds(isActive, userIds);
        return userIds;
    }

    @Transactional
    public StatResponseDto getAllStatistic() {
        Integer users = clinicRepository.countAllClinics();
        Integer diamonds = 0;
        Integer currency = clinicRepository.countAllCurrency();
        return StatResponseDto.builder()
            .users(users)
            .diamonds(diamonds)
            .lemons(currency)
            .build();
    }

    public Set<String> getUniqueJobTitle() {
        return userRepository.getUniqueJobTitles();
    }

    @Transactional
    public ClinicResponseDto postNewClinic(ClinicBaseDto clinicBaseDto) {
//        Optional<UserEntity> existedUser = userRepository.findByEmailContainingIgnoreCaseAndIsActiveIsTrue(userBaseDto.getEmail());
        Optional<ClinicEntity> existedCompany = clinicRepository.findByNameIgnoreCase(clinicBaseDto.getName());
        if (existedCompany.isPresent()) {
            throw new LemonBankException(ErrorType.CLINIC_ALREADY_EXIST);
        }
        ClinicEntity clinicEntity = clinicMapper.toClinicEntity(clinicBaseDto);
        ClinicEntity saved = clinicRepository.save(clinicEntity);
//        analitiqueService.saveAnalitique(
//            AnalitiqueType.NEW_EMPLOYER.getMessage(),
//            null,
//            null);
        return clinicMapper.toClinicResponseDto(saved);
    }

    public List<ClinicResponseDto> getClinicByName(String name, Pageable pageable) {
        String trimParameter = StringUtils.trimToEmpty(name);
        Page<ClinicEntity> clinicEntities =
            clinicRepository.findByNameContainingIgnoreCase(trimParameter, pageable);
        return clinicMapper.toClinicResponseDtoList(clinicEntities);
    }

    public ClinicResponseDto updateClinicCurrency(Integer id,
        ClinicCurrencyUpdateDto currencyUpdateDto) {
        Optional<ClinicEntity> clinic = clinicRepository.findById(id);
        if (clinic.isEmpty()) {
            throw new LemonBankException(ErrorType.CLINIC_NOT_FOUND);
        }
        ClinicEntity clinicEntity = clinic.get();
        Long currentCurrancy = clinicEntity.getCurrency();
        Integer differenceLemons = Math.toIntExact(
            currencyUpdateDto.getCurrency() - currentCurrancy);

        clinicEntity.setCurrency(currencyUpdateDto.getCurrency());

        ClinicEntity saved = clinicRepository.save(clinicEntity);

        historyService.changeCurrencyClinic(saved, differenceLemons, currencyUpdateDto.getComment());

//        String currency;
//        if (differenceDiamonds != 0) {
//            currency = "diamonds";
//            analitiqueService.saveAnalitique(AnalitiqueType.REWARD.getMessage(), differenceDiamonds, currency);
//        } else if (differenceLemons != 0) {
//            currency = "lemons";
//            analitiqueService.saveAnalitique(AnalitiqueType.REWARD.getMessage(), differenceLemons, currency);
//        }

        return clinicMapper.toClinicResponseDto(saved);
    }
}
