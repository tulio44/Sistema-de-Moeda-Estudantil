package com.moedaestudantil.config;

import com.moedaestudantil.service.security.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(AppUserDetailsService userDetailsService,
                          CustomAuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // públicas
                .requestMatchers(
                    "/login",
                    "/cadastro",
                    "/h2-console/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/webjars/**"
                ).permitAll()

                // aluno
                .requestMatchers("/ui/minhas-vantagens/**").hasRole("ALUNO")

                // vantagens para aluno
                .requestMatchers(
                    "/ui/vantagens/disponiveis",
                    "/ui/vantagens/*/resgatar"
                ).hasRole("ALUNO")

                // rotas da empresa
                .requestMatchers("/ui/vantagens/**").hasRole("EMPRESA")
                .requestMatchers("/ui/empresas/**").hasRole("EMPRESA")

                // demais telas web
                .requestMatchers("/ui/alunos/**").authenticated()

                // qualquer outra: logado
                .anyRequest().authenticated()
            )
            .headers(h -> h.frameOptions(f -> f.disable())) // H2
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler) // ✅ ESTE AQUI É O CERTO
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
        return builder.build();
    }
}
