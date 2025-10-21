package com.moedaestudantil.domain.model;
import jakarta.persistence.*;

@Entity public class Curso {
  @Id @GeneratedValue private Long id;
  @ManyToOne(optional=false) private Instituicao instituicao;
  @Column(nullable=false) private String nome;
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
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
}
