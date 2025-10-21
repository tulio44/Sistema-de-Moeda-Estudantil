package com.moedaestudantil.domain.model;
import jakarta.persistence.*;

@Entity
public class Professor {
  @Id @GeneratedValue private Long id;
  @OneToOne(optional=false) private User user;
  @ManyToOne(optional=false) private Instituicao instituicao;
  @Column(nullable=false) private String nome;
  @Column(nullable=false, unique=true, length=14) private String cpf;
  @Column(nullable=false) private String departamento;
  @Column(nullable=false) private Integer saldo = 0;
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
  public Long getVersao() {
    return versao;
  }
  public void setVersao(Long versao) {
    this.versao = versao;
  }
}