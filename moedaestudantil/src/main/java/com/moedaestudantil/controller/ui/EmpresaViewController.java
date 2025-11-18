package com.moedaestudantil.controller.ui;

import com.moedaestudantil.domain.model.Empresa;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.repo.EmpresaRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ui/empresas")
public class EmpresaViewController {

    private final EmpresaRepository empresaRepository;
    private final UserRepository userRepository;

    public EmpresaViewController(EmpresaRepository empresaRepository,
                                 UserRepository userRepository) {
        this.empresaRepository = empresaRepository;
        this.userRepository = userRepository;
    }

    /**
     * Mostrar / editar cadastro da empresa ligada ao usuário logado.
     */
    @GetMapping
    public String verOuEditarEmpresa(Model model) {
        User userLogado = getUsuarioLogado();

        Empresa empresa = empresaRepository.findByUser(userLogado)
                .orElseGet(() -> {
                    Empresa e = new Empresa();
                    e.setUser(userLogado);
                    return e;
                });

        model.addAttribute("empresa", empresa);
        return "empresas/form";
    }

    /**
     * Salvar empresa sempre vinculando ao usuário logado e
     * após salvar → redirecionar para /ui/vantagens.
     */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("empresa") Empresa form,
                         RedirectAttributes ra) {

        User userLogado = getUsuarioLogado();

        // Caso já exista, garantir update (não criar novo)
        var existenteOpt = empresaRepository.findByUser(userLogado);

        if (existenteOpt.isPresent()) {
            Empresa existente = existenteOpt.get();
            form.setId(existente.getId());
            form.setVersao(existente.getVersao());
        }

        // Sempre vinculação ao usuário logado
        form.setUser(userLogado);

        empresaRepository.save(form);

        ra.addFlashAttribute("msgOk", "Dados da empresa salvos com sucesso.");

        // 🔥 Redirecionamento correto após salvar
        return "redirect:/ui/vantagens";
    }

    /**
     * Excluir cadastro da empresa (opcional).
     */
    @PostMapping("/excluir")
    public String excluir(RedirectAttributes ra) {
        User userLogado = getUsuarioLogado();

        empresaRepository.findByUser(userLogado)
                .ifPresent(empresaRepository::delete);

        ra.addFlashAttribute("msgOk", "Cadastro de empresa excluído.");
        return "redirect:/ui/empresas";
    }

    // =============== Helpers ===============

    private User getUsuarioLogado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Usuário autenticado não encontrado."));
    }
}
