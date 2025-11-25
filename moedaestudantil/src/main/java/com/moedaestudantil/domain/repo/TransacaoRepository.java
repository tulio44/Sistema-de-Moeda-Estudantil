package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.Transacao;
import com.moedaestudantil.domain.model.enums.TipoTransacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    // Extrato paginado do professor (usado nas telas de UI)
    Page<Transacao> findByOrigemProfessorIdOrderByCriadoEmDesc(Long profId, Pageable p);

    // Extrato paginado do aluno (usado nas telas de UI)
    Page<Transacao> findByDestinoAlunoIdOrderByCriadoEmDesc(Long alunoId, Pageable p);

    // Usado na tela "minhas vantagens" para listar só resgates
    List<Transacao> findByDestinoAlunoAndTipoOrderByCriadoEmDesc(
            Aluno aluno,
            TipoTransacao tipo
    );

    // Usado pelo TransacaoService (API REST) – extratos simples
    List<Transacao> findByOrigemProfessorIdOrderByCriadoEmDesc(Long profId);

    List<Transacao> findByDestinoAlunoIdOrderByCriadoEmDesc(Long alunoId);
}
