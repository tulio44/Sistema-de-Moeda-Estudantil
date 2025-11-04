package com.moedaestudantil.domain.model;
import jakarta.persistence.*; import java.time.Instant;
import com.moedaestudantil.domain.model.enums.Role;

@Entity
@Table(name = "users") 
public class User {
  @Id @GeneratedValue private Long id;
  @Column(nullable=false, unique=true, length=180) private String email;
  @Column(nullable=false) private String senhaHash;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
  @Column(nullable=false) private Boolean ativo = true;
  @Column(nullable=false, updatable=false) private Instant criadoEm = Instant.now();
  // getters/setters...
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getSenhaHash() {
    return senhaHash;
  }
  public void setSenhaHash(String senhaHash) {
    this.senhaHash = senhaHash;
  }
  public Role getRole() {
    return role;
  }
  public void setRole(Role role) {
    this.role = role;
  }
  public Boolean getAtivo() {
    return ativo;
  }
  public void setAtivo(Boolean ativo) {
    this.ativo = ativo;
  }
  public Instant getCriadoEm() {
    return criadoEm;
  }
  public void setCriadoEm(Instant criadoEm) {
    this.criadoEm = criadoEm;
  }
}
