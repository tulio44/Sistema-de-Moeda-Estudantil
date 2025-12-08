package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.*;
import com.moedaestudantil.domain.model.enums.TipoTransacao;
import com.moedaestudantil.domain.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResgateVantagemService {

    private final AlunoRepository alunoRepo;
    private final TransacaoRepository txRepo;
    private final GeradorCupomService geradorCupomService;

    public ResgateVantagemService(AlunoRepository alunoRepo,
            TransacaoRepository txRepo,
            GeradorCupomService geradorCupomService) {
        this.alunoRepo = alunoRepo;
        this.txRepo = txRepo;
        this.geradorCupomService = geradorCupomService;
    }

    @Transactional
    public String executarResgate(Aluno aluno, Vantagem vantagem) {
        // Debita saldo do aluno
        aluno.setSaldo(aluno.getSaldo() - vantagem.getCusto());

        // Gera código do cupom
        String codigo = geradorCupomService.gerarCodigoCupom();

        // Cria transação de resgate
        Transacao tx = new Transacao();
        tx.setTipo(TipoTransacao.RESGATE_ALUNO);
        tx.setDestinoAluno(aluno);
        tx.setVantagem(vantagem);
        tx.setQuantidade(vantagem.getCusto());
        tx.setCodigoCupom(codigo);
        tx.setMensagem("Resgate da vantagem: " + vantagem.getTitulo());
        txRepo.save(tx);

        alunoRepo.save(aluno);
        return codigo;
    }
}
