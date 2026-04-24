package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.UnitMeasure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMeasureRepository extends JpaRepository<UnitMeasure, Long> {
}
