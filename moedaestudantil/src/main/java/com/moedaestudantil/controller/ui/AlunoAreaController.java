package com.moedaestudantil.controller.ui;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.Transacao;
import com.moedaestudantil.domain.model.enums.TipoTransacao;
import com.moedaestudantil.domain.repo.AlunoRepository;
import com.moedaestudantil.domain.repo.TransacaoRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ui/minhas-vantagens")
public class AlunoAreaController {

    private final UserRepository userRepo;
    private final AlunoRepository alunoRepo;
    private final TransacaoRepository transacaoRepo;

    public AlunoAreaController(UserRepository userRepo,
                               AlunoRepository alunoRepo,
                               TransacaoRepository transacaoRepo) {
        this.userRepo = userRepo;
        this.alunoRepo = alunoRepo;
        this.transacaoRepo = transacaoRepo;
    }

    @GetMapping
    public String home(Model model, RedirectAttributes ra) {

        Optional<Aluno> optAluno = getAlunoLogado();

        if (optAluno.isEmpty()) {
            ra.addFlashAttribute("erro",
                    "Sua conta de aluno ainda não está vinculada corretamente. Fale com o administrador.");
            return "redirect:/login?erro=sem-aluno";
        }

        Aluno aluno = optAluno.get();
        int saldo = aluno.getSaldo() != null ? aluno.getSaldo() : 0;

        List<Transacao> resgates = transacaoRepo
                .findByDestinoAlunoAndTipoOrderByCriadoEmDesc(aluno, TipoTransacao.RESGATE_ALUNO);

        model.addAttribute("aluno", aluno);
        model.addAttribute("saldo", saldo);
        model.addAttribute("resgates", resgates);

        return "alunos/minhas-vantagens";
    }

private Optional<Aluno> getAlunoLogado() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    var userOpt = userRepo.findByEmail(email);
    if (userOpt.isEmpty()) return Optional.empty();

    var alunoOpt = alunoRepo.findByUser(userOpt.get());

    // 🔹 se não existe aluno, não salva, apenas redireciona para o formulário
    if (alunoOpt.isEmpty()) return Optional.empty();

    return alunoOpt;
}


}
    