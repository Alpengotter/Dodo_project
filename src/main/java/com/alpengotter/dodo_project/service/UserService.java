package com.alpengotter.dodo_project.service;

import com.alpengotter.dodo_project.domain.dto.StatResponseDto;
import com.alpengotter.dodo_project.domain.dto.UserBaseDto;
import com.alpengotter.dodo_project.domain.dto.UserCurrencyMultipleUpdateDto;
import com.alpengotter.dodo_project.domain.dto.UserCurrencyUpdateDto;
import com.alpengotter.dodo_project.domain.dto.UserResponseDto;
import com.alpengotter.dodo_project.domain.dto.UserStatusMultipleUpdateDto;
import com.alpengotter.dodo_project.domain.dto.UserStatusUpdateDto;
import com.alpengotter.dodo_project.domain.entity.ClinicEntity;
import com.alpengotter.dodo_project.domain.entity.UserClinicMapEntity;
import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.domain.enums.AnalitiqueType;
import com.alpengotter.dodo_project.domain.mapper.UserMapper;
import com.alpengotter.dodo_project.domain.repository.ClinicRepository;
import com.alpengotter.dodo_project.domain.repository.UserClinicMapRepository;
import com.alpengotter.dodo_project.domain.repository.UserRepository;
import com.alpengotter.dodo_project.handler.ErrorType;
import com.alpengotter.dodo_project.handler.exception.LemonBankException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
public class UserService {

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final UserClinicMapRepository userClinicMapRepository;
    private final UserMapper userMapper;
    private final HistoryService historyService;
    private final AnalitiqueService analitiqueService;

    @Transactional
    public List<UserResponseDto> getAllUsers(Integer offset, Integer limit) {
        Page<UserEntity> users = userRepository.findAllAndIsActiveIsTrue(PageRequest.of(offset, limit));
        return userMapper.toListUserResponseDto(users);
    }

    @Transactional
    public UserResponseDto getUserById(Integer id) {
        Optional<UserEntity> user = userRepository.findByIdAndIsActiveIsTrue(id);
        if (user.isEmpty()) {
            throw new LemonBankException(ErrorType.USER_NOT_FOUND);
        }
        UserEntity userEntity = user.get();
        return userMapper.toUserResponseDto(userEntity);
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
    public List<UserResponseDto> getUserByParameter(String searchParameter, Integer[] clinicIds,
        Pageable pageable) {
        log.info("Start find by param:{}", searchParameter);
        String trimParameter = StringUtils.trimToEmpty(searchParameter);
//        log.info("Check is English Symbols");
//        if (isEnglishSymbols(trimParameter)) {
//            log.info("Find by email");
//            Optional<UserEntity> user = userRepository.findByEmailContainingIgnoreCaseAndIsActiveIsTrue(trimParameter);
//            if (user.isEmpty()) {
//                throw new LemonBankException(ErrorType.USER_NOT_FOUND);
//            }
//            return List.of(userMapper.toUserResponseDto(user.get()));
//        }
        log.info("Find by Name");
        Page<UserEntity> usersByFirstOrLastName =
            userRepository.findActiveUsersByFullNameAndClinics(
                trimParameter, trimParameter, trimParameter, clinicIds, pageable);
        if (usersByFirstOrLastName.isEmpty()) {
            throw new LemonBankException(ErrorType.USER_NOT_FOUND);
        }
        return userMapper.toListUserResponseDto(usersByFirstOrLastName);

    }

    @Transactional
    public UserResponseDto postNewUser(UserBaseDto userBaseDto) {
        // Убрано, так как теперь мы должны добавлять всех пользователей
//        Optional<UserEntity> existedUser = userRepository.findByEmailContainingIgnoreCaseAndIsActiveIsTrue(userBaseDto.getEmail());
//        if (existedUser.isPresent()) {
//            throw new LemonBankException(ErrorType.USER_ALREADY_EXIST);
//        }
        UserEntity userEntity = userMapper.toUserEntity(userBaseDto);
        Set<UserClinicMapEntity> userClinicMap = new HashSet<>(userClinicMapRepository.saveAll(
            getUserClinicMap(userBaseDto.getClinics(), userEntity)));
        userEntity.setUserClinicMap(userClinicMap);
        UserEntity saved = userRepository.save(userEntity);
//        analitiqueService.saveAnalitique(
//            AnalitiqueType.NEW_EMPLOYER.getMessage(),
//            null,
//            null);
        return userMapper.toUserResponseDto(saved);
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

        UserEntity saved = userRepository.saveAndFlush(userEntity);

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
//        // 1. Валидация
//        if (!"lemons".equals(updateDto.getCurrency())) {
//            throw new LemonBankException(ErrorType.NOT_CORRECT_CURRENCY);
//        }
//
//        List<Integer> userIds = updateDto.getUserIds();
//        Integer count = updateDto.getCount();
//
//        // 2. Получаем пользователей и клиники одним запросом
//        List<UserEntity> users = userRepository.findAllByIdInAndIsActiveIsTrueWithClinics(userIds);
//
//        // 3. Собираем все клиники
//        List<ClinicEntity> clinics = users.stream()
//            .flatMap(user -> user.getUserClinicMap().stream())
//            .map(UserClinicMapEntity::getClinic)
//            .collect(Collectors.toList());
//
//        // 4. Обновляем балансы
//        users.forEach(user -> user.setLemons(user.getLemons() + count));
//        clinics.forEach(clinic -> clinic.setCurrency(clinic.getCurrency() + count));
//
//        // 5. Массовое сохранение
//        userRepository.saveAll(users);
//        clinicRepository.saveAll(clinics);
//
//        // 6. Логирование
//        users.forEach(user ->
//            historyService.changeCurrency(user, count, 0, updateDto.getComment())
//        );
//        return userIds;
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
        Integer users = userRepository.countAllActiveUsers();
        Integer diamonds = userRepository.countAllDiamonds();
        Integer lemons = userRepository.countAllLemons();
        return StatResponseDto.builder()
            .users(users)
            .diamonds(diamonds)
            .lemons(lemons)
            .build();
    }

    public Set<String> getUniqueJobTitle() {
        return userRepository.getUniqueJobTitles();
    }

    private Set<UserClinicMapEntity> getUserClinicMap(List<Integer> clinicIds, UserEntity user) {
        Set<ClinicEntity> clinics = clinicRepository.findByIdIn(clinicIds);
        Set<UserClinicMapEntity> result = new HashSet<>();
        clinics.forEach(clinic -> {
                UserClinicMapEntity userClinicMapEntity = new UserClinicMapEntity();
                userClinicMapEntity.setUser(user);
                userClinicMapEntity.setClinic(clinic);
                result.add(userClinicMapEntity);
            });
        return result;
    }

    @Transactional
    public UserResponseDto updateEmployeeProfile(Integer id, UserBaseDto userBaseDto) {
        Optional<UserEntity> existedUser = userRepository.findByIdAndIsActiveIsTrue(id);
        if (existedUser.isEmpty()) {
            throw new LemonBankException(ErrorType.USER_NOT_FOUND);
        }
        UserEntity userEntity = existedUser.get();
        userMapper.updateUser(userBaseDto, userEntity, id);
        updateUserClinics(userEntity, userBaseDto);
        return userMapper.toUserResponseDto(userEntity);
    }

    private void updateUserClinics(UserEntity userEntity, UserBaseDto userBaseDto) {
        if (!userEntity.getUserClinicMap().isEmpty()) {
            userClinicMapRepository.deleteAll(userEntity.getUserClinicMap());
            userEntity.getUserClinicMap().clear();
        }

        // Создание новых связей
        if (userBaseDto.getClinics() != null && !userBaseDto.getClinics().isEmpty()) {
            Set<UserClinicMapEntity> newMappings = userBaseDto.getClinics().stream()
                .map(clinicRepository::findById)
                .flatMap(Optional::stream) // Java 9+
                .map(clinic -> UserClinicMapEntity.builder()
                    .user(userEntity)
                    .clinic(clinic)
                    .build())
                .collect(Collectors.toSet());

            userEntity.setUserClinicMap(newMappings);
            userClinicMapRepository.saveAll(newMappings);
        }
    }
}
