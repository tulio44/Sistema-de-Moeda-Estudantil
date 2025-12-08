package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.Professor;
import com.moedaestudantil.domain.model.Transacao;
import com.moedaestudantil.domain.repo.AlunoRepository;
import com.moedaestudantil.domain.repo.ProfessorRepository;
import com.moedaestudantil.domain.repo.TransacaoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransacaoService {
  private final ProfessorRepository professorRepository;
  private final AlunoRepository alunoRepository;
  private final TransacaoRepository transacaoRepository;

  public TransacaoService(
      ProfessorRepository professorRepository,
      AlunoRepository alunoRepository,
      TransacaoRepository transacaoRepository) {
    this.professorRepository = professorRepository;
    this.alunoRepository = alunoRepository;
    this.transacaoRepository = transacaoRepository;
  }

  @Transactional
  public Transacao enviarMoedas(Long professorId, Long alunoId, int quantidade, String mensagem) {
    if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
    if (mensagem == null || mensagem.isBlank())
      throw new IllegalArgumentException("Mensagem obrigatória");

    Professor professor =
        professorRepository
            .findById(professorId)
            .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    Aluno aluno =
        alunoRepository
            .findById(alunoId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

    // Aplicando Tell, Don't Ask: as entidades gerenciam seus próprios saldos
    professor.debitarMoedas(quantidade);
    aluno.creditarMoedas(quantidade);

    professorRepository.save(professor);
    alunoRepository.save(aluno);

    Transacao tx = new Transacao();
    tx.setTipo(com.moedaestudantil.domain.model.enums.TipoTransacao.ENVIO_PROFESSOR);
    tx.setOrigemProfessor(professor);
    tx.setDestinoAluno(aluno);
    tx.setQuantidade(quantidade);
    tx.setMensagem(mensagem);
    tx.setCriadoEm(java.time.Instant.now());
    transacaoRepository.save(tx);
    // TODO: notificar aluno por email
    return tx;
  }

  public List<Transacao> extratoProfessor(Long professorId) {
    return transacaoRepository.findByOrigemProfessorIdOrderByCriadoEmDesc(professorId);
  }

  public List<Transacao> extratoAluno(Long alunoId) {
    return transacaoRepository.findByDestinoAlunoIdOrderByCriadoEmDesc(alunoId);
  }
}
