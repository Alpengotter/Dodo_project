package com.alpengotter.dodo_project.domain.repository;

import com.alpengotter.dodo_project.domain.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query("select u from UserEntity u where u.email = lower(:email)")
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    @Query("select u from UserEntity u where "
        + "u.isActive = true "
        + "and lower(u.firstName) like lower(concat('%', :firstParameter, '%')) "
        + "and lower(u.lastName) like lower(concat('%', :secondParameter, '%'))")
    List<UserEntity> findByNameAndLastName(String firstParameter, String secondParameter);

    @Query("select u from UserEntity u where "
        + "u.isActive = true "
        + "and lower(u.firstName) like lower(concat('%', :firstName, '%'))")
    List<UserEntity> findByFirstName(String firstName);

    @Query("select u from UserEntity u where "
        + "u.isActive = true "
        + "and lower(u.lastName) like lower(concat('%', :lastName, '%'))")
    List<UserEntity> findByLastName(String lastName);

    Optional<UserEntity> findByEmailContainingIgnoreCaseAndIsActiveIsTrue(String email);

    @Query(value = "select u from UserEntity u where u.isActive = true order by u.lastName asc",
        countQuery = "select count(u) from UserEntity u where u.isActive = true")
    Page<UserEntity> findAllAndIsActiveIsTrue(Pageable pageRequest);

    List<UserEntity> findByIsActiveIsTrue();

    List<UserEntity> findAllByIdInAndIsActiveIsTrue(List<Integer> ids);

//    @Query(value = "select distinct u from UserEntity u "
//        + "left join u.userClinicMap ucm "
//        + "where (lower(u.firstName) like lower(concat('%', :firstName, '%')) "
//        + "or lower(u.lastName) like lower(concat('%', :lastName, '%')) "
//        + "or lower(u.surname) like lower(concat('%', :surname, '%'))) "
//        + "and (ucm.clinic.id IN :clinicIds) "
//        + "and u.isActive = true")
@Query("SELECT DISTINCT u FROM UserEntity u " +
    "WHERE " +
    "u.isActive = true " +
    "AND (" +
    "   :firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
    "   OR :lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) " +
    "   OR :surname IS NULL OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :surname, '%'))" +
    ")")
    Page<UserEntity> findActiveUsersByFullNameAndClinics(
        String firstName,
        String lastName,
        String surname,
        Pageable pageable);

    @Query("SELECT DISTINCT u FROM UserEntity u " +
        "WHERE " +
        "u.isActive = true " +
        "AND (" +
        "   :firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
        "   OR :lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) " +
        "   OR :jobTitle IS NULL OR LOWER(u.jobTitle) LIKE LOWER(CONCAT('%', :jobTitle, '%'))" +
        ")")
    Page<UserEntity> findActiveUsersByFullNameOrJobTitle(
        String firstName,
        String lastName,
        String jobTitle,
        Pageable pageable);
    Optional<UserEntity> findByIdAndIsActiveIsTrue(Integer id);

    @Modifying
    @Query("update UserEntity u set u.diamonds = ?1 where u.id in ?2")
    void updateDiamondsForIds(Integer currency, List<Integer> userIds);

    @Modifying
    @Query("update UserEntity u set u.lemons = ?1 where u.id in ?2")
    void updateLemonsForIds(Integer currency, List<Integer> userIds);

    @Modifying
    @Query("update UserEntity u set u.isActive = ?1 where u.id in ?2")
    void updateIsActiveForIds(boolean isActive, List<Integer> userIds);

    @Query("select COUNT(u) from UserEntity u where u.isActive = true ")
    Integer countAllActiveUsers();

    @Query("select SUM (u.diamonds) from UserEntity u where u.isActive = true ")
    Integer countAllDiamonds();

    @Query("select SUM (u.lemons) from UserEntity u where u.isActive = true ")
    Integer countAllLemons();

    @Query("select distinct u.jobTitle from UserEntity u "
        + "where u.jobTitle is not null "
        + "and trim(u.jobTitle) <> '' ")
    Set<String> getUniqueJobTitles();

    @Query("SELECT DISTINCT u FROM UserEntity u " +
        "LEFT JOIN FETCH u.userClinicMap ucm " +
        "LEFT JOIN FETCH ucm.clinic " +
        "WHERE u.id IN :userIds AND u.isActive = true")
    List<UserEntity> findAllByIdInAndIsActiveIsTrueWithClinics(List<Integer> userIds);
}
