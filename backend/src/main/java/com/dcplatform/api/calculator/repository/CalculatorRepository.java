package com.dcplatform.api.calculator.repository;

import com.dcplatform.api.calculator.model.CalculatorEstimateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CalculatorRepository extends JpaRepository<CalculatorEstimateEntity, UUID> {
}
