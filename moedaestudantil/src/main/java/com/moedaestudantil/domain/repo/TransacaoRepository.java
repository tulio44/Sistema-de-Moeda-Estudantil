package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.*;
import com.moedaestudantil.domain.model.enums.TipoTransacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    Page<Transacao> findByOrigemProfessorIdOrderByCriadoEmDesc(Long profId, Pageable p);

    Page<Transacao> findByDestinoAlunoIdOrderByCriadoEmDesc(Long alunoId, Pageable p);

    List<Transacao> findByDestinoAlunoAndTipoOrderByCriadoEmDesc(
            Aluno aluno,
            TipoTransacao tipo
    );
}
