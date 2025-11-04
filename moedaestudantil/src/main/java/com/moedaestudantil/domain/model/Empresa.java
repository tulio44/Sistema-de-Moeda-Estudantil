package com.moedaestudantil.domain.model;

import jakarta.persistence.*;

@Entity
public class Empresa {

  @Id
  @GeneratedValue
  private Long id;

  @Version
  private Long versao; // <- usado no form para controle otimista

@ManyToOne(optional = false)
@JoinColumn(name = "user_id", nullable = false) // sem unique
private User user;

  @Column(nullable = false, unique = true)
  private String cnpj;

  @Column(nullable = false)
  private String nomeFantasia;

  private String emailContato;
  private String endereco;
  private String telefone; // <- para listar no template

  // Getters/Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getVersao() { return versao; }
  public void setVersao(Long versao) { this.versao = versao; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public String getCnpj() { return cnpj; }
  public void setCnpj(String cnpj) { this.cnpj = cnpj; }

  public String getNomeFantasia() { return nomeFantasia; }
  public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }

  public String getEmailContato() { return emailContato; }
  public void setEmailContato(String emailContato) { this.emailContato = emailContato; }

  public String getEndereco() { return endereco; }
  public void setEndereco(String endereco) { this.endereco = endereco; }

  public String getTelefone() { return telefone; }
  public void setTelefone(String telefone) { this.telefone = telefone; }

  // Alias pra ${e.razaoSocial} se seu list.html usa isso:
  public String getRazaoSocial() { return this.nomeFantasia; }
  public void setRazaoSocial(String razaoSocial) { this.nomeFantasia = razaoSocial; }
}
