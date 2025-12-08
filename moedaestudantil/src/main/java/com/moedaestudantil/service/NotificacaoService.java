package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.*;
import com.moedaestudantil.domain.repo.NotificacaoEmailRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class NotificacaoService {

    private final NotificacaoEmailRepository notifRepo;

    public NotificacaoService(NotificacaoEmailRepository notifRepo) {
        this.notifRepo = notifRepo;
    }

    public void notificarMoedaRecebida(Aluno aluno, int quantidade, String motivo) {
        NotificacaoEmail n = new NotificacaoEmail();
        n.setTipo("MOEDA_RECEBIDA");
        n.setDestinatarioEmail(aluno.getUser().getEmail());
        n.setPayloadJson("{\"qtd\":" + quantidade + ",\"mensagem\":\"" + safe(motivo) + "\"}");
        notifRepo.save(n);
    }

    public void notificarCupomParaAluno(Aluno aluno, Vantagem vantagem, String codigo) {
        NotificacaoEmail n = new NotificacaoEmail();
        n.setTipo("CUPOM_ALUNO");
        n.setDestinatarioEmail(aluno.getUser().getEmail());
        n.setPayloadJson("{\"titulo\":\"" + safe(vantagem.getTitulo()) + "\",\"codigo\":\"" + codigo + "\"}");
        notifRepo.save(n);
    }

    public void notificarCupomParaEmpresa(Aluno aluno, Vantagem vantagem, String codigo) {
        String emailEmpresa = Optional.ofNullable(vantagem.getEmpresa().getEmailContato()).orElse("");
        NotificacaoEmail n = new NotificacaoEmail();
        n.setTipo("CUPOM_EMPRESA");
        n.setDestinatarioEmail(emailEmpresa);
        n.setPayloadJson(
                "{\"titulo\":\"" + safe(vantagem.getTitulo()) + "\"," +
                        "\"codigo\":\"" + codigo + "\"," +
                        "\"aluno\":\"" + safe(aluno.getNome()) + "\"}");
        notifRepo.save(n);
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}