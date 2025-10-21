package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditoSemestralRepository extends JpaRepository<CreditoSemestral, Long> {
  boolean existsByProfessorIdAndAnoRefAndSemestreRef(Long professorId, Integer anoRef, Integer semestreRef);
}
