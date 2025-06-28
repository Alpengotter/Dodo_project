package com.alpengotter.dodo_project.domain.repository;

import com.alpengotter.dodo_project.domain.entity.ClinicEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClinicRepository extends JpaRepository<ClinicEntity, Integer> {

    Optional<ClinicEntity> findByNameIgnoreCase(String name);
    Page<ClinicEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<ClinicEntity> findAll(Pageable pageRequest);
    Set<ClinicEntity> findByIdIn(List<Integer> clinicIds);

    @Query("select COUNT(c) from ClinicEntity c")
    Integer countAllClinics();

    @Query("select SUM (с.currency) from ClinicEntity с")
    Integer countAllCurrency();
}
