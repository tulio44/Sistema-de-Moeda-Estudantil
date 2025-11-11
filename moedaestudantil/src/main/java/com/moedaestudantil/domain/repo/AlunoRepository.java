package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByUser(User user);
}
