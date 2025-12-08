package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.CreditoSemestral;
import com.moedaestudantil.domain.model.Professor;
import com.moedaestudantil.domain.model.Transacao;
import com.moedaestudantil.domain.model.enums.TipoTransacao;
import com.moedaestudantil.domain.repo.CreditoSemestralRepository;
import com.moedaestudantil.domain.repo.ProfessorRepository;
import com.moedaestudantil.domain.repo.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class CreditoSemestralService {

    private final ProfessorRepository profRepo;
    private final CreditoSemestralRepository credRepo;
    private final TransacaoRepository txRepo;

    public CreditoSemestralService(ProfessorRepository profRepo,
                                   CreditoSemestralRepository credRepo,
                                   TransacaoRepository txRepo) {
        this.profRepo = profRepo;
        this.credRepo = credRepo;
        this.txRepo = txRepo;
    }

    /**
     * Credita 1000 moedas ao professor (ano/semestre).
     */
    @Transactional
    public void creditar(Long professorId, int ano, int semestre) {
        if (semestre != 1 && semestre != 2) {
            throw new IllegalArgumentException("semestre inválido (use 1 ou 2)");
        }

        boolean jaExiste = credRepo
                .existsByProfessorIdAndAnoRefAndSemestreRef(professorId, ano, semestre);
        if (jaExiste) {
            throw new IllegalStateException("crédito já lançado para " + ano + "/" + semestre);
        }

        Professor prof = profRepo.findById(professorId)
                .orElseThrow(() -> new NoSuchElementException("Professor não encontrado: " + professorId));

        // 1) Atualiza saldo (acumulável)
        int qtd = 1000;
        prof.setSaldo(prof.getSaldo() + qtd);

        // 2) Transação de crédito semestral USANDO O BUILDER
        Transacao tx = Transacao.builder(TipoTransacao.CREDITO_SEMESTRAL)
                .origemProfessor(prof)
                .quantidade(qtd)
                .mensagem("Crédito semestral " + ano + "/" + semestre)
                .build();
        txRepo.save(tx);

        // 3) Registro de bloqueio (evitar duplicidade)
        CreditoSemestral c = new CreditoSemestral();
        c.setProfessor(prof);
        c.setAnoRef(ano);
        c.setSemestreRef(semestre);
        credRepo.save(c);

        // 4) Persiste saldo novo
        profRepo.save(prof);
    }

    @Transactional(readOnly = true)
    public boolean jaCreditado(Long professorId, int ano, int semestre) {
        return credRepo.existsByProfessorIdAndAnoRefAndSemestreRef(professorId, ano, semestre);
    }
}
