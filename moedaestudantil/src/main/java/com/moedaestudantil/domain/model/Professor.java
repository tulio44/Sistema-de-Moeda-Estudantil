package com.moedaestudantil.domain.model;

import jakarta.persistence.*;

@Entity
public class Professor {

  @Id @GeneratedValue private Long id;

  @Version private Long versao;

  // Igual Empresa: cada professor tem UM usuário responsável pelo login
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(optional = false)
  private Instituicao instituicao;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false, unique = true, length = 14)
  private String cpf;

  @Column(nullable = false)
  private String departamento;

  @Column(nullable = false)
  private Integer saldo = 0;

  // Getters e Setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getVersao() {
    return versao;
  }

  public void setVersao(Long versao) {
    this.versao = versao;
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

  public String getDepartamento() {
    return departamento;
  }

  public void setDepartamento(String departamento) {
    this.departamento = departamento;
  }

  public Integer getSaldo() {
    return saldo;
  }

  public void setSaldo(Integer saldo) {
    this.saldo = saldo;
  }

  /**
   * Verifica se o professor possui saldo suficiente para realizar uma transferência.
   *
   * @param quantidade quantidade de moedas a verificar
   * @return true se o saldo é suficiente, false caso contrário
   */
  public boolean possuiSaldoSuficiente(int quantidade) {
    return this.saldo >= quantidade;
  }

  /**
   * Debita uma quantidade de moedas do saldo do professor. Este método aplica o princípio Tell,
   * Don't Ask.
   *
   * @param quantidade quantidade de moedas a debitar
   * @throws IllegalArgumentException se o saldo for insuficiente
   */
  public void debitarMoedas(int quantidade) {
    if (!possuiSaldoSuficiente(quantidade)) {
      throw new IllegalArgumentException(
          "Professor não possui saldo suficiente. Saldo atual: " + this.saldo);
    }
    this.saldo -= quantidade;
  }
}
