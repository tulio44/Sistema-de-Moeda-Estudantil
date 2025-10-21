package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.CreditoSemestral;
import com.moedaestudantil.domain.model.Professor;
import com.moedaestudantil.domain.model.Transacao;
import com.moedaestudantil.domain.repo.CreditoSemestralRepository;
import com.moedaestudantil.domain.repo.ProfessorRepository;
import com.moedaestudantil.domain.repo.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class CreditoSemestralService {

  private final ProfessorRepository profRepo;
  private final CreditoSemestralRepository credRepo;
  private final TransacaoRepository txRepo;

  public CreditoSemestralService(ProfessorRepository profRepo,
                                 CreditoSemestralRepository credRepo,
                                 TransacaoRepository txRepo) {
    this.profRepo = profRepo;
    this.credRepo = credRepo;
    this.txRepo = txRepo;
  }

  /**
   * Credita 1000 moedas ao professor para o (ano, semestre) informado.
   * Regras:
   *  - semestre deve ser 1 ou 2;
   *  - só pode haver um crédito por professor/ano/semestre;
   *  - o saldo do professor é acrescido;
   *  - é registrada uma Transacao do tipo CREDITO_SEMESTRAL;
   *  - é gravado um registro em CreditoSemestral para bloquear duplicidade.
   */
  @Transactional
  public void creditar(Long professorId, int ano, int semestre) {
    if (semestre != 1 && semestre != 2) {
      throw new IllegalArgumentException("semestre inválido (use 1 ou 2)");
    }

    boolean jaExiste = credRepo
        .existsByProfessorIdAndAnoRefAndSemestreRef(professorId, ano, semestre);
    if (jaExiste) {
      throw new IllegalStateException("crédito já lançado para " + ano + "/" + semestre);
    }

    Professor prof = profRepo.findById(professorId)
        .orElseThrow(() -> new NoSuchElementException("Professor não encontrado: " + professorId));

    // 1) Atualiza saldo (acumulável)
    prof.setSaldo(prof.getSaldo() + 1000);

    // 2) Registra transação
    txRepo.save(Transacao.credito(prof, 1000, ano, semestre));

    // 3) Registra bloqueio (garante unicidade lógica)
    CreditoSemestral c = new CreditoSemestral();
    c.setProfessor(prof);
    c.setAnoRef(ano);
    c.setSemestreRef(semestre);
    credRepo.save(c);

    // 4) Persiste o novo saldo
    profRepo.save(prof);
  }

  /** Útil para a UI/Controller checar se já foi creditado. */
  @Transactional(readOnly = true)
  public boolean jaCreditado(Long professorId, int ano, int semestre) {
    return credRepo.existsByProfessorIdAndAnoRefAndSemestreRef(professorId, ano, semestre);
  }
}
