package com.moedaestudantil.service.security;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.Empresa;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.model.enums.Role;
import com.moedaestudantil.domain.repo.AlunoRepository;
import com.moedaestudantil.domain.repo.EmpresaRepository;
import com.moedaestudantil.domain.repo.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AlunoRepository alunoRepository;
    private final EmpresaRepository empresaRepository;

    public LoginSuccessHandler(UserRepository userRepository,
            AlunoRepository alunoRepository,
            EmpresaRepository empresaRepository) {
        this.userRepository = userRepository;
        this.alunoRepository = alunoRepository;
        this.empresaRepository = empresaRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        String redirectUrl = "/"; // fallback padrão

        // ======================
        // 🚀 ROLE EMPRESA
        // ======================
        if (user.getRole() == Role.EMPRESA) {

            Empresa empresa = empresaRepository.findByUser(user).orElse(null);

            // Se empresa NÃO estiver preenchida → vai para o form
            if (empresa == null || empresa.getCnpj() == null || empresa.getNomeFantasia() == null) {
                redirectUrl = "/ui/empresas";
            } else {
                // Se estiver preenchida → vai para listagem de vantagens
                redirectUrl = "/ui/vantagens";
            }
        }

        // ======================
        // 🚀 ROLE PROFESSOR
        // ======================
        else if (user.getRole() == Role.PROFESSOR) {
            redirectUrl = "/ui/professores";
        }

        // ======================
        // 🚀 ROLE ALUNO
        // ======================
        else if (user.getRole() == Role.ALUNO) {

            Aluno aluno = alunoRepository.findByUser(user).orElse(null);

            if (aluno == null ||
                    aluno.getNome() == null ||
                    aluno.getCurso() == null ||
                    aluno.getInstituicao() == null) {

                redirectUrl = "/ui/alunos/novo"; // cadastro incompleto

            } else {
                redirectUrl = "/ui/minhas-vantagens"; // página padrão do aluno
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
