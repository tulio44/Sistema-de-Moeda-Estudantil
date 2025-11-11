package com.moedaestudantil.domain.model;

import com.moedaestudantil.domain.model.enums.TipoTransacao;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @ManyToOne
    @JoinColumn(name = "origem_professor_id")
    private Professor origemProfessor;

    @ManyToOne
    @JoinColumn(name = "destino_aluno_id")
    private Aluno destinoAluno;

    @ManyToOne
    @JoinColumn(name = "vantagem_id")
    private Vantagem vantagem;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(length = 500)
    private String mensagem;

    @Column(name = "codigo_cupom", length = 64)
    private String codigoCupom;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm = Instant.now();

    // getters e setters

    public Long getId() { return id; }

    public TipoTransacao getTipo() { return tipo; }
    public void setTipo(TipoTransacao tipo) { this.tipo = tipo; }

    public Professor getOrigemProfessor() { return origemProfessor; }
    public void setOrigemProfessor(Professor origemProfessor) { this.origemProfessor = origemProfessor; }

    public Aluno getDestinoAluno() { return destinoAluno; }
    public void setDestinoAluno(Aluno destinoAluno) { this.destinoAluno = destinoAluno; }

    public Vantagem getVantagem() { return vantagem; }
    public void setVantagem(Vantagem vantagem) { this.vantagem = vantagem; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getCodigoCupom() { return codigoCupom; }
    public void setCodigoCupom(String codigoCupom) { this.codigoCupom = codigoCupom; }

    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
}
