package com.moedaestudantil.domain.model;
import jakarta.persistence.*;

@Entity
public class Empresa {
  @Id @GeneratedValue private Long id;
  @OneToOne(optional=false) private User user;
  @Column(nullable=false, unique=true) private String cnpj;
  @Column(nullable=false) private String nomeFantasia;
  private String emailContato; private String endereco;
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
  public String getCnpj() {
    return cnpj;
  }
  public void setCnpj(String cnpj) {
    this.cnpj = cnpj;
  }
  public String getNomeFantasia() {
    return nomeFantasia;
  }
  public void setNomeFantasia(String nomeFantasia) {
    this.nomeFantasia = nomeFantasia;
  }
  public String getEmailContato() {
    return emailContato;
  }
  public void setEmailContato(String emailContato) {
    this.emailContato = emailContato;
  }
  public String getEndereco() {
    return endereco;
  }
  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }
}