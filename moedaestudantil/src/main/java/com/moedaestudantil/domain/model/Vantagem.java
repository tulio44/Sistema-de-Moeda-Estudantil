package com.moedaestudantil.domain.model;
import jakarta.persistence.*;

@Entity
public class Vantagem {
  @Id @GeneratedValue private Long id;
  @ManyToOne(optional=false) private Empresa empresa;
  @Column(nullable=false) private String titulo;
  @Lob private String descricao;
  private String fotoUrl;
  @Column(nullable=false) private Integer custo;
  @Column(nullable=false) private Boolean ativo = true;
  // g/s...
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public Empresa getEmpresa() {
    return empresa;
  }
  public void setEmpresa(Empresa empresa) {
    this.empresa = empresa;
  }
  public String getTitulo() {
    return titulo;
  }
  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }
  public String getDescricao() {
    return descricao;
  }
  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }
  public String getFotoUrl() {
    return fotoUrl;
  }
  public void setFotoUrl(String fotoUrl) {
    this.fotoUrl = fotoUrl;
  }
  public Integer getCusto() {
    return custo;
  }
  public void setCusto(Integer custo) {
    this.custo = custo;
  }
  public Boolean getAtivo() {
    return ativo;
  }
  public void setAtivo(Boolean ativo) {
    this.ativo = ativo;
  }

}