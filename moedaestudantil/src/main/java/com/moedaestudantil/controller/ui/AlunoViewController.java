package com.moedaestudantil.controller.ui;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.repo.AlunoRepository;
import com.moedaestudantil.domain.repo.TransacaoRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ui/alunos")
public class AlunoViewController {

    private final UserRepository userRepo;
    private final AlunoRepository alunoRepo;
    private final TransacaoRepository txRepo;

    public AlunoViewController(UserRepository userRepo,
                               AlunoRepository alunoRepo,
                               TransacaoRepository txRepo) {
        this.userRepo = userRepo;
        this.alunoRepo = alunoRepo;
        this.txRepo = txRepo;
    }

    @GetMapping("/extrato")
    public String extratoAluno(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Aluno aluno = getAlunoLogado();

        var extrato =
                txRepo.findByDestinoAlunoIdOrderByCriadoEmDesc(
                        aluno.getId(),
                        PageRequest.of(page, 20)
                );

        model.addAttribute("aluno", aluno);
        model.addAttribute("extrato", extrato);

        return "alunos/extrato";
    }

    private Aluno getAlunoLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication()
                .getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado"));

        return alunoRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Aluno não encontrado"));
    }
}
