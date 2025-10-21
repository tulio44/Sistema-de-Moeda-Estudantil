package com.moedaestudantil.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import com.moedaestudantil.domain.model.enums.TipoTransacao;

@Entity
public class Transacao {
  @Id @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoTransacao tipo;

  @ManyToOne
  private Professor origemProfessor;   // ENVIO / CREDITO

  @ManyToOne
  private Aluno destinoAluno;          // ENVIO / RESGATE

  @ManyToOne
  private Vantagem vantagem;           // RESGATE

  @Column(nullable = false)
  private Integer quantidade;

  private String mensagem;             // motivo (envio) ou "Crédito ano/sem"
  private String codigoCupom;          // resgate

  @Column(nullable = false, updatable = false)
  private Instant criadoEm = Instant.now();

  // ===== FÁBRICAS sem 'var' =====
  public static Transacao envio(Professor p, Aluno a, int qtd, String msg) {
    Transacao t = new Transacao();
    t.tipo = TipoTransacao.ENVIO_PROFESSOR;
    t.origemProfessor = p;
    t.destinoAluno = a;
    t.quantidade = qtd;
    t.mensagem = msg;
    return t;
  }

  public static Transacao resgate(Aluno a, Vantagem v, String cupom) {
    Transacao t = new Transacao();
    t.tipo = TipoTransacao.RESGATE_ALUNO;
    t.destinoAluno = a;
    t.vantagem = v;
    t.quantidade = v.getCusto();
    t.codigoCupom = cupom;
    return t;
  }

  public static Transacao credito(Professor p, int qtd, int ano, int sem) {
    Transacao t = new Transacao();
    t.tipo = TipoTransacao.CREDITO_SEMESTRAL;
    t.origemProfessor = p;
    t.quantidade = qtd;
    t.mensagem = "Crédito " + ano + "/" + sem;
    return t;
  }

  // ===== getters/setters =====
  public Long getId() { return id; }
  public TipoTransacao getTipo() { return tipo; }
  public void setTipo(TipoTransacao tipo) { this.tipo = tipo; }
  public Professor getOrigemProfessor() { return origemProfessor; }
  public void setOrigemProfessor(Professor origemProfessor) { this.origemProfessor = origemProfessor; }
  public Aluno getDestinoAluno() { return destinoAluno; }
  public void setDestinoAluno(Aluno destinoAluno) { this.destinoAluno = destinoAluno; }
  public Vantagem getVantagem() { return vantagem; }
  public void setVantagem(Vantagem vantagem) { this.vantagem = vantagem; }
  public Integer getQuantidade() { return quantidade; }
  public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
  public String getMensagem() { return mensagem; }
  public void setMensagem(String mensagem) { this.mensagem = mensagem; }
  public String getCodigoCupom() { return codigoCupom; }
  public void setCodigoCupom(String codigoCupom) { this.codigoCupom = codigoCupom; }
  public Instant getCriadoEm() { return criadoEm; }
  public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
}
