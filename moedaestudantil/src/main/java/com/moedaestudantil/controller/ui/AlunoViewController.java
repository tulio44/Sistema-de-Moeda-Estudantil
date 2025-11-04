package com.moedaestudantil.controller.ui;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.model.enums.Role;
import com.moedaestudantil.domain.repo.AlunoRepository;
import com.moedaestudantil.domain.repo.CursoRepository;
import com.moedaestudantil.domain.repo.InstituicaoRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;

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
    public String list(Model model, @ModelAttribute("msg") String msg, @ModelAttribute("erro") String erro) {
        model.addAttribute("alunos", repo.findAll());
        // os flash attributes "msg" e "erro" já chegam no modelo
        return "alunos/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("instituicoes", instRepo.findAll());
        model.addAttribute("cursos", cursoRepo.findAll());
        model.addAttribute("isEdicao", false);
        return "alunos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var aluno = repo.findById(id).orElseThrow();
        model.addAttribute("aluno", aluno);
        model.addAttribute("instituicoes", instRepo.findAll());
        model.addAttribute("cursos", cursoRepo.findAll());
        model.addAttribute("isEdicao", true);
        return "alunos/form";
    }

    @PostMapping("/{id}/excluir")
    @Transactional
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            repo.deleteById(id);
            ra.addFlashAttribute("msg", "Aluno excluído com sucesso.");
        } catch (DataIntegrityViolationException e) {
            // Há transações referenciando este aluno (FK constraint)
            ra.addFlashAttribute("erro", "Não é possível excluir: existem registros vinculados (ex.: transações).");
        }
        return "redirect:/ui/alunos";
    }

    @PostMapping
    @Transactional
    public String salvar(@ModelAttribute Aluno aluno,
                         @RequestParam(value = "email", required = false) String email,
                         @RequestParam(value = "senha", required = false) String senha,
                         Model model,
                         RedirectAttributes ra) {

        boolean criando = (aluno.getId() == null);

        if (criando) {
            // criação: email e senha obrigatórios
            if (!StringUtils.hasText(email) || !StringUtils.hasText(senha)) {
                model.addAttribute("aluno", aluno);
                model.addAttribute("instituicoes", instRepo.findAll());
                model.addAttribute("cursos", cursoRepo.findAll());
                model.addAttribute("isEdicao", false);
                model.addAttribute("erro", "Email e senha são obrigatórios ao criar o aluno.");
                return "alunos/form";
            }

            User u = new User();
            u.setEmail(email.trim());
            u.setSenhaHash("{noop}" + senha); // ajuste para encoder real se tiver
            u.setRole(Role.ALUNO);
            u.setAtivo(true);
            u.setCriadoEm(Instant.now());
            u = userRepo.save(u);

            aluno.setUser(u);
            repo.save(aluno);
            ra.addFlashAttribute("msg", "Aluno criado com sucesso.");
            return "redirect:/ui/alunos";
        } else {
            // edição: manter user existente; e-mail/senha opcionais
            Aluno original = repo.findById(aluno.getId()).orElseThrow();

            // copiar campos editáveis do formulário
            original.setNome(aluno.getNome());
            original.setCpf(aluno.getCpf());
            original.setRg(aluno.getRg());
            original.setEndereco(aluno.getEndereco());
            original.setInstituicao(aluno.getInstituicao());
            original.setCurso(aluno.getCurso());

            User u = original.getUser();
            if (u == null) {
                // Se por acaso o aluno não tinha user ainda, permite criar agora se informou email/senha
                if (StringUtils.hasText(email) && StringUtils.hasText(senha)) {
                    u = new User();
                    u.setEmail(email.trim());
                    u.setSenhaHash("{noop}" + senha);
                    u.setRole(Role.ALUNO);
                    u.setAtivo(true);
                    u.setCriadoEm(Instant.now());
                    u = userRepo.save(u);
                    original.setUser(u);
                }
            } else {
                // Atualizar email se preenchido
                if (StringUtils.hasText(email)) {
                    u.setEmail(email.trim());
                }
                // Atualizar senha se preenchida
                if (StringUtils.hasText(senha)) {
                    u.setSenhaHash("{noop}" + senha);
                }
                userRepo.save(u);
            }

            repo.save(original);
            ra.addFlashAttribute("msg", "Aluno atualizado com sucesso.");
            return "redirect:/ui/alunos";
        }
    }
}
