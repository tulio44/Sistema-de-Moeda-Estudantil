package com.moedaestudantil.domain.model;

import jakarta.persistence.*;

@Entity
public class Aluno {
  @Id @GeneratedValue private Long id;

  @OneToOne(optional = false)
  private User user;

  @ManyToOne(optional = false)
  private Instituicao instituicao;

  @ManyToOne(optional = false)
  private Curso curso;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false, unique = true, length = 14)
  private String cpf;

  private String rg;
  private String endereco;

  @Column(nullable = false)
  private Integer saldo = 0;

  @Version private Long versao;

  // g/s...
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Instituicao getInstituicao() {
    return instituicao;
  }

  public void setInstituicao(Instituicao instituicao) {
    this.instituicao = instituicao;
  }

  public Curso getCurso() {
    return curso;
  }

  public void setCurso(Curso curso) {
    this.curso = curso;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public String getRg() {
    return rg;
  }

  public void setRg(String rg) {
    this.rg = rg;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }

  public Integer getSaldo() {
    return saldo;
  }

  public void setSaldo(Integer saldo) {
    this.saldo = saldo;
  }

  public Long getVersao() {
    return versao;
  }

  public void setVersao(Long versao) {
    this.versao = versao;
  }

  /**
   * Credita uma quantidade de moedas ao saldo do aluno. Este método aplica o princípio Tell, Don't
   * Ask.
   *
   * @param quantidade quantidade de moedas a creditar
   * @throws IllegalArgumentException se a quantidade for negativa ou zero
   */
  public void creditarMoedas(int quantidade) {
    if (quantidade <= 0) {
      throw new IllegalArgumentException("Quantidade deve ser positiva");
    }
    this.saldo += quantidade;
  }
}
