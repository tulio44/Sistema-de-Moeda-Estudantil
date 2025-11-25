package com.moedaestudantil.controller.ui;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.Professor;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.repo.AlunoRepository;
import com.moedaestudantil.domain.repo.ProfessorRepository;
import com.moedaestudantil.domain.repo.TransacaoRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import com.moedaestudantil.service.MoedaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ui/professores")
public class ProfessorViewController {

    private final UserRepository userRepo;
    private final ProfessorRepository profRepo;
    private final AlunoRepository alunoRepo;
    private final TransacaoRepository txRepo;
    private final MoedaService moedaService;

    public ProfessorViewController(
            UserRepository userRepo,
            ProfessorRepository profRepo,
            AlunoRepository alunoRepo,
            TransacaoRepository txRepo,
            MoedaService moedaService
    ) {
        this.userRepo = userRepo;
        this.profRepo = profRepo;
        this.alunoRepo = alunoRepo;
        this.txRepo = txRepo;
        this.moedaService = moedaService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("professor", getProfessorLogado());
        return "professores/home";
    }

    @GetMapping("/enviar")
    public String enviarForm(Model model) {

        Professor prof = getProfessorLogado();

        List<Aluno> alunos =
                alunoRepo.findByInstituicao(prof.getInstituicao());

        model.addAttribute("professor", prof);
        model.addAttribute("alunos", alunos);

        return "professores/enviar";
    }

    @PostMapping("/enviar")
    public String enviarMoedas(
            @RequestParam long alunoId,
            @RequestParam int quantidade,
            @RequestParam String motivo,
            RedirectAttributes ra
    ) {
        try {
            Professor prof = getProfessorLogado();
            moedaService.enviarMoedas(prof.getId(), alunoId, quantidade, motivo);
            ra.addFlashAttribute("msgOk", "Moedas enviadas com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("msgErro", e.getMessage());
        }

        return "redirect:/ui/professores/enviar";
    }

@GetMapping("/extrato")
public String extratoProfessor(
        @RequestParam(defaultValue = "0") int page,
        Model model
) {
    Professor prof = getProfessorLogado();

    var extrato =
            txRepo.findByOrigemProfessorIdOrderByCriadoEmDesc(
                    prof.getId(),
                    PageRequest.of(page, 20)
            );

    // DEBUG PARA SABER O QUE ESTÁ VINDO DO BANCO
    System.out.println("===== DEBUG DO EXTRATO DO PROFESSOR =====");
    extrato.forEach(tx -> {
        System.out.println(
                "ID=" + tx.getId()
                        + " | TIPO=" + tx.getTipo()
                        + " | QTD=" + tx.getQuantidade()
                        + " | ALUNO=" +
                        (tx.getDestinoAluno() != null
                                ? tx.getDestinoAluno().getNome()
                                : "null")
        );
    });
    System.out.println("==========================================");

    model.addAttribute("professor", prof);
    model.addAttribute("extrato", extrato);

    return "professores/extrato";
}



    // ================= HELPERS ==================

    private Professor getProfessorLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado"));
        return profRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Professor não encontrado"));
    }
}
