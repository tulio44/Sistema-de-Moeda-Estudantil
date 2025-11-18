package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.Instituicao;
import com.moedaestudantil.domain.model.Professor;
import com.moedaestudantil.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByUser(User user);

    List<Professor> findByInstituicao(Instituicao instituicao);
}
