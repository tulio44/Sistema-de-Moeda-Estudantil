package com.moedaestudantil.service.security;

import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.model.enums.Role;
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

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        String redirectUrl = "/"; // fallback padrão

        if (user.getRole() == Role.EMPRESA) {
            redirectUrl = "/ui/empresas/home";
        } else if (user.getRole() == Role.ALUNO) {
            redirectUrl = "/ui/minhas-vantagens";

        response.sendRedirect(redirectUrl);
    }
}
}
