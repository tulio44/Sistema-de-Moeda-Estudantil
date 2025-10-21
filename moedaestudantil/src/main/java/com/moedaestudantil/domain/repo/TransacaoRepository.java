package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
  Page<Transacao> findByOrigemProfessorIdOrderByCriadoEmDesc(Long profId, Pageable p);
  Page<Transacao> findByDestinoAlunoIdOrderByCriadoEmDesc(Long alunoId, Pageable p);
}
