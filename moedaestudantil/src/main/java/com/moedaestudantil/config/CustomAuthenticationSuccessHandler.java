package com.moedaestudantil.config;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.model.enums.Role;
import com.moedaestudantil.domain.repo.AlunoRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepo;
    private final AlunoRepository alunoRepo;

    public CustomAuthenticationSuccessHandler(UserRepository userRepo,
                                              AlunoRepository alunoRepo) {
        this.userRepo = userRepo;
        this.alunoRepo = alunoRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String email = authentication.getName();
        User u = userRepo.findByEmail(email).orElseThrow();

        // 🔹 EMPRESA → vai pro form da empresa
        if (u.getRole() == Role.EMPRESA) {
            response.sendRedirect("/ui/empresas");
            return;
        }

        // 🔹 ALUNO → decide conforme o cadastro
        if (u.getRole() == Role.ALUNO) {
            Aluno aluno = alunoRepo.findByUser(u).orElse(null);

            if (aluno == null ||
                aluno.getNome() == null ||
                aluno.getCurso() == null ||
                aluno.getInstituicao() == null) {
                // sem cadastro completo → tela de preenchimento
                response.sendRedirect("/ui/alunos/novo");
            } else {
                // já tem cadastro completo → área de vantagens
                response.sendRedirect("/ui/minhas-vantagens");
            }
            return;
        }

        // fallback pra outros tipos
        response.sendRedirect("/login");
    }
}
