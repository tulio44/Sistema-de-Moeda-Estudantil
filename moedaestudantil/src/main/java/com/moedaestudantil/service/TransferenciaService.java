package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.*;
import com.moedaestudantil.domain.model.enums.TipoTransacao;
import com.moedaestudantil.domain.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferenciaService {

    private final ProfessorRepository profRepo;
    private final AlunoRepository alunoRepo;
    private final TransacaoRepository txRepo;

    public TransferenciaService(ProfessorRepository profRepo,
            AlunoRepository alunoRepo,
            TransacaoRepository txRepo) {
        this.profRepo = profRepo;
        this.alunoRepo = alunoRepo;
        this.txRepo = txRepo;
    }

    @Transactional
    public void executarTransferencia(Professor professor, Aluno aluno, int quantidade, String motivo) {
        // Atualiza saldos
        professor.setSaldo(professor.getSaldo() - quantidade);
        aluno.setSaldo(aluno.getSaldo() + quantidade);

        // Cria transação
        Transacao tx = new Transacao();
        tx.setTipo(TipoTransacao.ENVIO_PROFESSOR);
        tx.setOrigemProfessor(professor);
        tx.setDestinoAluno(aluno);
        tx.setQuantidade(quantidade);
        tx.setMensagem(motivo);
        txRepo.save(tx);

        // Persiste entidades
        profRepo.save(professor);
        alunoRepo.save(aluno);
    }
}