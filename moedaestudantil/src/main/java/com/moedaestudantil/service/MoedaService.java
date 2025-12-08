package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MoedaService {

    private final ValidadorTransacao validador;
    private final TransferenciaService transferenciaService;
    private final ResgateVantagemService resgateService;
    private final NotificacaoService notificacaoService;

    public MoedaService(ValidadorTransacao validador,
            TransferenciaService transferenciaService,
            ResgateVantagemService resgateService,
            NotificacaoService notificacaoService) {
        this.validador = validador;
        this.transferenciaService = transferenciaService;
        this.resgateService = resgateService;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public void enviarMoedas(Long professorId, Long alunoId, int qtd, String motivo) {
        // 1. Validação
        validador.validarEnvioMoedas(professorId, alunoId, qtd, motivo);

        // 2. Busca e validação de entidades
        Professor prof = validador.buscarEValidarProfessor(professorId, qtd);
        Aluno aluno = validador.buscarAluno(alunoId);

        // 3. Execução da transferência
        transferenciaService.executarTransferencia(prof, aluno, qtd, motivo);

        // 4. Notificação
        notificacaoService.notificarMoedaRecebida(aluno, qtd, motivo);
    }

    @Transactional
    public String resgatarVantagem(Long alunoId, Long vantagemId) {
        // 1. Validação
        validador.validarResgateVantagem(alunoId, vantagemId);

        // 2. Busca de entidades
        Aluno aluno = validador.buscarAluno(alunoId);
        Vantagem vantagem = validador.buscarEValidarVantagem(vantagemId, aluno.getSaldo());

        // 3. Execução do resgate
        String codigo = resgateService.executarResgate(aluno, vantagem);

        // 4. Notificações
        notificacaoService.notificarCupomParaAluno(aluno, vantagem, codigo);
        notificacaoService.notificarCupomParaEmpresa(aluno, vantagem, codigo);

        return codigo;
    }
}
