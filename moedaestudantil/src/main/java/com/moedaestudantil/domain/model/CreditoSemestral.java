package com.moedaestudantil.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"professor_id","anoRef","semestreRef"}))
@Getter @Setter @NoArgsConstructor
public class CreditoSemestral {
  @Id @GeneratedValue
  private Long id;

  @ManyToOne(optional = false)
  private Professor professor;

  @Column(nullable = false)
  private Integer anoRef;

  @Column(nullable = false)
  private Integer semestreRef; // 1 ou 2

  @Column(nullable = false, updatable = false)
  private Instant creditadoEm = Instant.now();
}
