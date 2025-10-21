package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {}
