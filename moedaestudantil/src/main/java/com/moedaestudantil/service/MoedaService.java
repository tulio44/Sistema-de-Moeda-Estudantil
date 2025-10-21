package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.*;
import com.moedaestudantil.domain.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class MoedaService {
  private final ProfessorRepository profRepo;
  private final AlunoRepository alunoRepo;
  private final VantagemRepository vantRepo;
  private final TransacaoRepository txRepo;
  private final NotificacaoEmailRepository notifRepo;

  public MoedaService(ProfessorRepository p, AlunoRepository a, VantagemRepository v,
                      TransacaoRepository t, NotificacaoEmailRepository n) {
    this.profRepo=p; this.alunoRepo=a; this.vantRepo=v; this.txRepo=t; this.notifRepo=n;
  }

  @Transactional
  public void enviarMoedas(Long professorId, Long alunoId, int qtd, String motivo) {
    if (qtd <= 0) throw new IllegalArgumentException("qtd inválida");
    if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("motivo obrigatório");

    Professor prof = profRepo.findById(professorId)
        .orElseThrow(() -> new NoSuchElementException("Professor não encontrado: " + professorId));
    Aluno aluno = alunoRepo.findById(alunoId)
        .orElseThrow(() -> new NoSuchElementException("Aluno não encontrado: " + alunoId));

    if (prof.getSaldo() < qtd) throw new IllegalStateException("saldo insuficiente");
    prof.setSaldo(prof.getSaldo() - qtd);
    aluno.setSaldo(aluno.getSaldo() + qtd);

    txRepo.save(Transacao.envio(prof, aluno, qtd, motivo));

    NotificacaoEmail n = new NotificacaoEmail();
    n.setTipo("MOEDA_RECEBIDA");
    n.setDestinatarioEmail(aluno.getUser().getEmail());
    n.setPayloadJson("{\"qtd\":" + qtd + ",\"mensagem\":\"" + motivo.replace("\"","\\\"") + "\"}");
    notifRepo.save(n);

    profRepo.save(prof);
    alunoRepo.save(aluno);
  }

  @Transactional
  public String resgatarVantagem(Long alunoId, Long vantagemId) {
    Aluno aluno = alunoRepo.findById(alunoId)
        .orElseThrow(() -> new NoSuchElementException("Aluno não encontrado: " + alunoId));
    Vantagem v = vantRepo.findById(vantagemId)
        .orElseThrow(() -> new NoSuchElementException("Vantagem não encontrada: " + vantagemId));

    if (!Boolean.TRUE.equals(v.getAtivo())) throw new IllegalStateException("vantagem inativa");
    if (aluno.getSaldo() < v.getCusto()) throw new IllegalStateException("saldo insuficiente");

    aluno.setSaldo(aluno.getSaldo() - v.getCusto());
    String codigo = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

    txRepo.save(Transacao.resgate(aluno, v, codigo));

    NotificacaoEmail n1 = new NotificacaoEmail();
    n1.setTipo("CUPOM_ALUNO");
    n1.setDestinatarioEmail(aluno.getUser().getEmail());
    n1.setPayloadJson("{\"titulo\":\"" + safe(v.getTitulo()) + "\",\"codigo\":\"" + codigo + "\"}");
    notifRepo.save(n1);

    String emailEmpresa = Optional.ofNullable(v.getEmpresa().getEmailContato()).orElse("");
    NotificacaoEmail n2 = new NotificacaoEmail();
    n2.setTipo("CUPOM_EMPRESA");
    n2.setDestinatarioEmail(emailEmpresa);
    n2.setPayloadJson("{\"titulo\":\"" + safe(v.getTitulo()) + "\",\"codigo\":\"" + codigo +
                      "\",\"aluno\":\"" + safe(aluno.getNome()) + "\"}");
    notifRepo.save(n2);

    alunoRepo.save(aluno);
    return codigo;
  }

  private String safe(String s) { return s == null ? "" : s.replace("\"","\\\""); }
}
