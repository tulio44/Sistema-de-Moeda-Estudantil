package com.moedaestudantil.controller;

import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.model.enums.Role;
import com.moedaestudantil.domain.repo.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // já tá funcionando
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute("userForm") UserForm form,
                            RedirectAttributes ra) {

        if (form.getEmail() == null || form.getEmail().isBlank()
                || form.getSenha() == null || form.getSenha().isBlank()
                || form.getRole() == null || form.getRole().isBlank()) {
            ra.addFlashAttribute("erro", "Preencha todos os campos.");
            return "redirect:/cadastro";
        }

        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            ra.addFlashAttribute("erro", "E-mail já cadastrado.");
            return "redirect:/cadastro";
        }

        Role role = Role.valueOf(form.getRole()); // ALUNO ou EMPRESA

        User u = new User();
        u.setEmail(form.getEmail().trim());
        u.setSenhaHash(form.getSenha()); // TEXTO PURO
        u.setRole(role);
        u.setAtivo(true);

        userRepository.save(u);

        ra.addFlashAttribute("msgOk", "Usuário criado. Faça login.");
        return "redirect:/login";
    }

    // DTO simples pro form
    public static class UserForm {
        private String email;
        private String senha;
        private String role;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
