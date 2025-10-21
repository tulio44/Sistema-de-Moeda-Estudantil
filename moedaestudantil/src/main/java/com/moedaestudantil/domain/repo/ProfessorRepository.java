package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {}
