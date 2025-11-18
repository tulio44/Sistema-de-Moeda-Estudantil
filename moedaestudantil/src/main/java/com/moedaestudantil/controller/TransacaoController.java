package com.moedaestudantil.controller;

import com.moedaestudantil.domain.model.Transacao;
import com.moedaestudantil.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {
    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<Transacao> enviarMoedas(@RequestParam Long professorId,
                                                  @RequestParam Long alunoId,
                                                  @RequestParam int quantidade,
                                                  @RequestParam String mensagem) {
        Transacao tx = transacaoService.enviarMoedas(professorId, alunoId, quantidade, mensagem);
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/extrato/professor/{id}")
    public ResponseEntity<List<Transacao>> extratoProfessor(@PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.extratoProfessor(id));
    }

    @GetMapping("/extrato/aluno/{id}")
    public ResponseEntity<List<Transacao>> extratoAluno(@PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.extratoAluno(id));
    }
}
