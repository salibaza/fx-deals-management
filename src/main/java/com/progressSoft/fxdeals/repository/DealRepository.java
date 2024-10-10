package com.progressSoft.fxdeals.repository;

import com.progressSoft.fxdeals.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealRepository extends JpaRepository<Deal, String> {
    boolean existsByUniqueId(String uniqueId);
}