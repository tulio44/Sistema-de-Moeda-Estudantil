package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.*;
import com.moedaestudantil.domain.repo.*;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
public class ValidadorTransacao {

    private final ProfessorRepository profRepo;
    private final AlunoRepository alunoRepo;
    private final VantagemRepository vantRepo;

    public ValidadorTransacao(ProfessorRepository profRepo,
            AlunoRepository alunoRepo,
            VantagemRepository vantRepo) {
        this.profRepo = profRepo;
        this.alunoRepo = alunoRepo;
        this.vantRepo = vantRepo;
    }

    public void validarEnvioMoedas(Long professorId, Long alunoId, int quantidade, String motivo) {
        if (quantidade <= 0)
            throw new IllegalArgumentException("qtd inválida");
        if (motivo == null || motivo.isBlank())
            throw new IllegalArgumentException("motivo obrigatório");
        if (professorId == null)
            throw new IllegalArgumentException("ID do professor não pode ser nulo");
        if (alunoId == null)
            throw new IllegalArgumentException("ID do aluno não pode ser nulo");
    }

    public void validarResgateVantagem(Long alunoId, Long vantagemId) {
        if (alunoId == null)
            throw new IllegalArgumentException("ID do aluno não pode ser nulo");
        if (vantagemId == null)
            throw new IllegalArgumentException("ID da vantagem não pode ser nulo");
    }

    public Professor buscarEValidarProfessor(Long professorId, int quantidade) {
        Professor prof = profRepo.findById(professorId)
                .orElseThrow(() -> new NoSuchElementException("Professor não encontrado: " + professorId));

        if (prof.getSaldo() < quantidade) {
            throw new IllegalStateException("saldo insuficiente");
        }
        return prof;
    }

    public Aluno buscarAluno(Long alunoId) {
        return alunoRepo.findById(alunoId)
                .orElseThrow(() -> new NoSuchElementException("Aluno não encontrado: " + alunoId));
    }

    public Vantagem buscarEValidarVantagem(Long vantagemId, int saldoAluno) {
        Vantagem v = vantRepo.findById(vantagemId)
                .orElseThrow(() -> new NoSuchElementException("Vantagem não encontrada: " + vantagemId));

        if (!Boolean.TRUE.equals(v.getAtivo())) {
            throw new IllegalStateException("vantagem inativa");
        }
        if (saldoAluno < v.getCusto()) {
            throw new IllegalStateException("saldo insuficiente");
        }
        return v;
    }
}