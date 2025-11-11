package com.moedaestudantil.service.security;

import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (Boolean.FALSE.equals(u.getAtivo())) {
            throw new UsernameNotFoundException("Usuário inativo");
        }

        String role = "ROLE_" + u.getRole().name();

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getSenhaHash())   // aqui vem {noop}senha ou outro formato válido
                .authorities(role)
                .build();
    }
}
