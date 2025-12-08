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

    // Construtor padrão protegido para JPA
    protected Transacao() {}
    
    // Getters e Setters (mantidos para JPA/Compatibilidade)
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
    
    // ===================================
    // ✨ IMPLEMENTAÇÃO DO BUILDER PATTERN
    // ===================================
    
    /** Ponto de entrada do Builder. */
    public static TransacaoBuilder builder(TipoTransacao tipo) {
        return new TransacaoBuilder(tipo);
    }

    public static class TransacaoBuilder {
        private final Transacao instance;

        /** Inicializa o Builder com o tipo de transação (obrigatório). */
        public TransacaoBuilder(TipoTransacao tipo) {
            this.instance = new Transacao();
            this.instance.setTipo(tipo);
            this.instance.setCriadoEm(Instant.now());
        }

        public TransacaoBuilder origemProfessor(Professor origemProfessor) {
            this.instance.setOrigemProfessor(origemProfessor);
            return this;
        }

        public TransacaoBuilder destinoAluno(Aluno destinoAluno) {
            this.instance.setDestinoAluno(destinoAluno);
            return this;
        }

        public TransacaoBuilder vantagem(Vantagem vantagem) {
            this.instance.setVantagem(vantagem);
            return this;
        }

        public TransacaoBuilder quantidade(Integer quantidade) {
            this.instance.setQuantidade(quantidade);
            return this;
        }

        public TransacaoBuilder mensagem(String mensagem) {
            this.instance.setMensagem(mensagem);
            return this;
        }

        public TransacaoBuilder codigoCupom(String codigoCupom) {
            this.instance.setCodigoCupom(codigoCupom);
            return this;
        }

        /** Cria e retorna a instância de Transacao com os parâmetros definidos. */
        public Transacao build() {
            // Garante campos básicos
            if (this.instance.getTipo() == null || this.instance.getQuantidade() == null || this.instance.getQuantidade() <= 0) {
                throw new IllegalStateException("Transacao inválida: tipo e quantidade são obrigatórios e positivos.");
            }
            return this.instance;
        }
    }
}