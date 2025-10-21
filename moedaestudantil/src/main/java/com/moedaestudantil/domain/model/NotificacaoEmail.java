package com.moedaestudantil.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor
public class NotificacaoEmail {
  @Id @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String tipo; // MOEDA_RECEBIDA, CUPOM_ALUNO, CUPOM_EMPRESA

  @Column(nullable = false)
  private String destinatarioEmail;

  @Lob
  private String payloadJson;

  @Column(nullable = false)
  private String status = "PENDENTE";

  @Column(nullable = false, updatable = false)
  private Instant criadoEm = Instant.now();
}
