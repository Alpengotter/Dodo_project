package com.alpengotter.dodo_project.domain.repository;

import com.alpengotter.dodo_project.domain.entity.HistoryEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer> {

    @Query("select h from HistoryEntity h "
        + "left join UserEntity u "
        + "on u.id = h.user.id "
        + "where (h.date between :dateFrom and :dateTo) "
        + "and (lower(u.email) like lower(concat('%', :email, '%')))"
        + "order by h.id desc ")
    List<HistoryEntity> findAllByDateBetweenAndUserEmailContainingOrderByIdDesc(
        LocalDateTime dateFrom,
        LocalDateTime dateTo,
        String email);

    @Query("select h from HistoryEntity h "
        + "where (lower(h.comment) like lower(concat('%', :comment, '%')) "
        + "and cast(h.date as date) between cast(:dateFrom as date) and cast(:dateTo as date)) "
        + "order by h.id desc")
    List<HistoryEntity> findByComment(
        String comment,
        LocalDateTime dateFrom,
        LocalDateTime dateTo);

    @Query("select h from HistoryEntity h "
        + "left join UserEntity u "
        + "on u.id = h.user.id "
        + "left join UserClinicMapEntity ucm "
        + "on ucm.user.id = h.user.id "
        + "where (cast(h.date as date) between cast(:dateFrom as date) and cast(:dateTo as date)) "
        + "and ((lower(u.firstName) like lower(concat('%', :firstName, '%'))) "
        + "or (lower(u.lastName) like lower(concat('%', :lastName, '%'))) "
        + "or (lower(u.surname) like lower(concat('%', :surname, '%'))) "
        + "or (lower(h.comment) like lower(concat('%', :comment, '%')))) "
        + "and (:clinicIds is null or ucm.clinic.id in :clinicIds) "
        + "order by h.id desc ")
    Set<HistoryEntity> findAllByDateBetweenAndUserFirstNameContainingOrUserLastNameContainingOrderByIdDesc(
        LocalDateTime dateFrom,
        LocalDateTime dateTo,
        String firstName,
        String lastName,
        String surname,
        String comment,
        List<Integer> clinicIds);

    @Query("select distinct h.comment from HistoryEntity h "
        + "where (:year IS NULL OR YEAR(h.date) = :year) ")
    List<String> findUniqueCommentsByYear(Integer year);

    List<HistoryEntity> findAllByUserIdOrderByIdDesc(Integer id);

    List<HistoryEntity> findAllByClinicIdOrderByIdDesc(Integer id);

    @Query("select abs(sum(h.value)) from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'lemons'"
        + "and h.value < 0")
    Integer countLemonsSpend(Integer month, Integer year);

    @Query("select sum(h.value) from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'lemons'"
        + "and h.value > 0")
    Integer countLemonsAccrued(Integer month, Integer year);

    @Query("select abs(sum(h.value)) from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'diamonds'"
        + "and h.value < 0")
    Integer countDiamondsSpend(Integer month, Integer year);

    @Query("select sum(h.value) from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'diamonds'"
        + "and h.value > 0")
    Integer countDiamondsAccrued(Integer month, Integer year);

    @Query("select h from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'lemons'"
        + "and h.value < 0")
    List<HistoryEntity> findByLemonsSpend(Integer month, Integer year);

    @Query("select h from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'lemons' "
        + "and h.value > 0 "
        + "and h.user is not null")
    List<HistoryEntity> findByLemonsAccruedByUser(Integer month, Integer year);

    @Query("select h from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'lemons' "
        + "and h.value > 0 "
        + "and h.clinic is not null")
    List<HistoryEntity> findByLemonsAccruedByClinic(Integer month, Integer year);

    @Query("select h from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'lemons' "
        + "and h.value > 0 "
        + "and h.comment = :comment ")
    List<HistoryEntity> findByLemonsAccruedByComment(Integer month, Integer year, String comment);

    @Query("select h from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'diamonds'"
        + "and h.value < 0")
    List<HistoryEntity> findByDiamondsSpend(Integer month, Integer year);

    @Query("select h from HistoryEntity h "
        + "where YEAR(h.date) = :year "
        + "and (:month IS NULL OR MONTH(h.date) = :month) "
        + "and h.currency = 'diamonds'"
        + "and h.value > 0")
    List<HistoryEntity> findByDiamondsAccrued(Integer month, Integer year);
}
