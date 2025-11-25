package com.moedaestudantil.controller.ui;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.Empresa;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.model.Vantagem;
import com.moedaestudantil.domain.repo.AlunoRepository;
import com.moedaestudantil.domain.repo.EmpresaRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import com.moedaestudantil.domain.repo.VantagemRepository;
import com.moedaestudantil.service.MoedaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/ui/vantagens")
public class VantagemController {

    private final VantagemRepository vantagemRepo;
    private final UserRepository userRepo;
    private final EmpresaRepository empresaRepo;
    private final AlunoRepository alunoRepo;
    private final MoedaService moedaService;

    public VantagemController(VantagemRepository vantagemRepo,
                              UserRepository userRepo,
                              EmpresaRepository empresaRepo,
                              AlunoRepository alunoRepo,
                              MoedaService moedaService) {
        this.vantagemRepo = vantagemRepo;
        this.userRepo = userRepo;
        this.empresaRepo = empresaRepo;
        this.alunoRepo = alunoRepo;
        this.moedaService = moedaService;
    }

    // ================== ÁREA EMPRESA ==================

    @GetMapping
    public String listarDaEmpresa(Model model) {
        Empresa empresa = getEmpresaLogada();
        model.addAttribute("vantagens", vantagemRepo.findByEmpresa(empresa));
        return "vantagens/list";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("vantagem", new Vantagem());
        return "vantagens/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Empresa empresa = getEmpresaLogada();
        if (id == null) throw new IllegalArgumentException("ID da vantagem não pode ser nulo");
        Vantagem v = vantagemRepo.findById(id).orElse(null);
        if (v == null || !v.getEmpresa().getId().equals(empresa.getId())) {
            ra.addFlashAttribute("erro", "Vantagem não encontrada.");
            return "redirect:/ui/vantagens";
        }
        model.addAttribute("vantagem", v);
        return "vantagens/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Vantagem vantagem,
                         @RequestParam(value = "imagemArquivo", required = false) MultipartFile imagemArquivo,
                         RedirectAttributes ra) {

        Empresa empresa = getEmpresaLogada();

        Vantagem alvo;
        if (vantagem.getId() == null) {
            alvo = new Vantagem();
            alvo.setEmpresa(empresa);
            alvo.setAtivo(true);
        } else {
            Long vid = vantagem.getId();
            if (vid == null) throw new IllegalArgumentException("ID da vantagem não pode ser nulo");
            alvo = vantagemRepo.findById(vid).orElse(null);
            if (alvo == null || !alvo.getEmpresa().getId().equals(empresa.getId())) {
                ra.addFlashAttribute("erro", "Vantagem inválida.");
                return "redirect:/ui/vantagens";
            }
        }

        alvo.setTitulo(vantagem.getTitulo());
        alvo.setDescricao(vantagem.getDescricao());
        alvo.setCusto(vantagem.getCusto());
        alvo.setAtivo(vantagem.getAtivo() != null ? vantagem.getAtivo() : true);

        if (imagemArquivo != null && !imagemArquivo.isEmpty()) {
            try {
                String base64 = Base64.getEncoder().encodeToString(imagemArquivo.getBytes());
                alvo.setFotoUrl(base64);
            } catch (IOException e) {
                ra.addFlashAttribute("erro", "Erro ao processar imagem.");
                return "redirect:/ui/vantagens";
            }
        }

        vantagemRepo.save(alvo);
        ra.addFlashAttribute("msgOk", "Vantagem salva com sucesso.");
        return "redirect:/ui/vantagens";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        Empresa empresa = getEmpresaLogada();
        if (id == null) throw new IllegalArgumentException("ID da vantagem não pode ser nulo");
        Vantagem v = vantagemRepo.findById(id).orElse(null);
        if (v == null || !v.getEmpresa().getId().equals(empresa.getId())) {
            ra.addFlashAttribute("erro", "Vantagem inválida.");
            return "redirect:/ui/vantagens";
        }
        vantagemRepo.delete(v);
        ra.addFlashAttribute("msgOk", "Vantagem excluída.");
        return "redirect:/ui/vantagens";
    }

    // ================== ÁREA ALUNO ==================

    @GetMapping("/disponiveis")
    public String listarDisponiveis(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepo.findByEmail(email).orElse(null);
        Aluno aluno = null;
        if (u != null) {
            aluno = alunoRepo.findByUser(u).orElse(null);
        }
        model.addAttribute("aluno", aluno);
        model.addAttribute("vantagens", vantagemRepo.findByAtivoTrue());
        return "vantagens/disponiveis";
    }

    @PostMapping("/{id}/resgatar")
    public String resgatar(@PathVariable Long id,
                           RedirectAttributes ra) {

        // Usuário logado -> Aluno
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User u = userRepo.findByEmail(email).orElse(null);
        if (u == null) {
            ra.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/ui/vantagens/disponiveis";
        }

        Aluno aluno = alunoRepo.findByUser(u).orElse(null);
        if (aluno == null) {
            ra.addFlashAttribute("erro", "Nenhum aluno vinculado a este usuário.");
            return "redirect:/ui/vantagens/disponiveis";
        }

        try {
            String codigo = moedaService.resgatarVantagem(aluno.getId(), id);
            if (id == null) throw new IllegalArgumentException("ID da vantagem não pode ser nulo");
            Vantagem v = vantagemRepo.findById(id).orElse(null);
            String titulo = (v != null ? v.getTitulo() : "vantagem");
            ra.addFlashAttribute("msgOk",
                    "Vantagem '" + titulo + "' resgatada com sucesso! Código do cupom: " + codigo);
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        } catch (NoSuchElementException e) {
            ra.addFlashAttribute("erro", "Vantagem ou aluno não encontrado.");
        }

        return "redirect:/ui/vantagens/disponiveis";
    }

    // ================== HELPER ==================

    private Empresa getEmpresaLogada() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado."));
        return empresaRepo.findByUser(u)
                .orElseThrow(() -> new IllegalStateException("Nenhuma empresa vinculada a este usuário."));
    }
}
