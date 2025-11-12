package com.moedaestudantil.controller.ui;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.model.enums.Role;
import com.moedaestudantil.domain.repo.AlunoRepository;
import com.moedaestudantil.domain.repo.CursoRepository;
import com.moedaestudantil.domain.repo.InstituicaoRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.Optional;

@Controller
@RequestMapping("/ui/alunos")
public class AlunoViewController {

    private final AlunoRepository repo;
    private final InstituicaoRepository instRepo;
    private final CursoRepository cursoRepo;
    private final UserRepository userRepo;

    public AlunoViewController(AlunoRepository repo,
                               InstituicaoRepository instRepo,
                               CursoRepository cursoRepo,
                               UserRepository userRepo) {
        this.repo = repo;
        this.instRepo = instRepo;
        this.cursoRepo = cursoRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public String list(Model model,
                       @ModelAttribute("msg") String msg,
                       @ModelAttribute("erro") String erro) {
        model.addAttribute("alunos", repo.findAll());
        return "alunos/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Aluno aluno = new Aluno();

        // tenta preencher email automaticamente com usuário logado (caso seja fluxo de auto-cadastro)
        getUsuarioLogado().ifPresent(u -> aluno.setUser(u));

        model.addAttribute("aluno", aluno);
        model.addAttribute("instituicoes", instRepo.findAll());
        model.addAttribute("cursos", cursoRepo.findAll());
        return "alunos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var aluno = repo.findById(id).orElseThrow();
        model.addAttribute("aluno", aluno);
        model.addAttribute("instituicoes", instRepo.findAll());
        model.addAttribute("cursos", cursoRepo.findAll());
        return "alunos/form";
    }

    @PostMapping("/{id}/excluir")
    @Transactional
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            repo.deleteById(id);
            ra.addFlashAttribute("msg", "Aluno excluído com sucesso.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("erro", "Não é possível excluir: existem registros vinculados (ex.: transações).");
        }
        return "redirect:/ui/alunos";
    }

    @PostMapping
    @Transactional
    public String salvar(@ModelAttribute Aluno aluno,
                         @RequestParam(value = "email", required = false) String email,
                         @RequestParam(value = "senha", required = false) String senha,
                         RedirectAttributes ra,
                         Model model) {

        boolean criando = (aluno.getId() == null);

        // quem está logado agora?
        Optional<User> optLogado = getUsuarioLogado();

        if (criando) {

            // 🔹 Fluxo: usuário ALUNO já está logado e só faltava completar o cadastro
            if (optLogado.isPresent() && optLogado.get().getRole() == Role.ALUNO) {
                User u = optLogado.get();

                // se o cara mudar o email no form, atualiza
                if (StringUtils.hasText(email)) {
                    u.setEmail(email.trim());
                }

                // se informar senha, troca; se deixar em branco, mantém
                if (StringUtils.hasText(senha)) {
                    u.setSenhaHash("{noop}" + senha);
                }

                userRepo.save(u);

                aluno.setUser(u);
                repo.save(aluno);

                ra.addFlashAttribute("msg", "Cadastro de aluno concluído com sucesso.");
                return "redirect:/ui/minhas-vantagens";
            }

            // 🔹 Fluxo: criação via painel/adm → precisa criar user junto
            if (!StringUtils.hasText(email) || !StringUtils.hasText(senha)) {
                model.addAttribute("aluno", aluno);
                model.addAttribute("instituicoes", instRepo.findAll());
                model.addAttribute("cursos", cursoRepo.findAll());
                model.addAttribute("erro", "Email e senha são obrigatórios ao criar o aluno.");
                return "alunos/form";
            }

            User u = new User();
            u.setEmail(email.trim());
            u.setSenhaHash("{noop}" + senha);
            u.setRole(Role.ALUNO);
            u.setAtivo(true);
            u.setCriadoEm(Instant.now());
            u = userRepo.save(u);

            aluno.setUser(u);
            repo.save(aluno);

            ra.addFlashAttribute("msg", "Aluno criado com sucesso.");
            return "redirect:/ui/alunos";
        }

        // 🔹 Edição
        Aluno original = repo.findById(aluno.getId()).orElseThrow();

        original.setNome(aluno.getNome());
        original.setCpf(aluno.getCpf());
        original.setRg(aluno.getRg());
        original.setEndereco(aluno.getEndereco());
        original.setInstituicao(aluno.getInstituicao());
        original.setCurso(aluno.getCurso());

        User u = original.getUser();

        // pode acontecer de não ter user ainda (arrumar aqui também)
        if (u == null && StringUtils.hasText(email) && StringUtils.hasText(senha)) {
            u = new User();
            u.setEmail(email.trim());
            u.setSenhaHash("{noop}" + senha);
            u.setRole(Role.ALUNO);
            u.setAtivo(true);
            u.setCriadoEm(Instant.now());
            u = userRepo.save(u);
            original.setUser(u);
        } else if (u != null) {
            if (StringUtils.hasText(email)) {
                u.setEmail(email.trim());
            }
            if (StringUtils.hasText(senha)) {
                u.setSenhaHash("{noop}" + senha);
            }
            userRepo.save(u);
        }

        repo.save(original);
        ra.addFlashAttribute("msg", "Aluno atualizado com sucesso.");
        return "redirect:/ui/minhas-vantagens";
    }

    // =========== helpers ===========

    private Optional<User> getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();
        }
        return userRepo.findByEmail(auth.getName());
    }
}
