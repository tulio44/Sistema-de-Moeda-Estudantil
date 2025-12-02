package com.moedaestudantil;

import com.moedaestudantil.domain.model.*;
import com.moedaestudantil.domain.model.enums.Role;
import com.moedaestudantil.domain.repo.*;
import com.moedaestudantil.service.CreditoSemestralService;
import com.moedaestudantil.service.MoedaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class Bootstrap {

    @Bean
    CommandLineRunner demoData(
            InstituicaoRepository instRepo,
            CursoRepository cursoRepo,
            UserRepository userRepo,
            ProfessorRepository profRepo,
            AlunoRepository alunoRepo,
            EmpresaRepository empRepo,
            VantagemRepository vantRepo,
            TransacaoRepository txRepo,
            CreditoSemestralService creditoSrv,
            MoedaService moedaSrv
    ) {
        return args -> {
            System.out.println("==== INICIANDO BOOTSTRAP ====");

            // Instituição
            Instituicao inst = new Instituicao();
            inst.setNome("Universidade X");
            inst = instRepo.save(inst);

            // Curso
            Curso cco = new Curso();
            cco.setInstituicao(inst);
            cco.setNome("Computação");
            cco = cursoRepo.save(cco);

            // Usuário Professor
            User uProf = new User();
            uProf.setEmail("prof@uni.x");
            uProf.setSenhaHash("{noop}senha");
            uProf.setRole(Role.PROFESSOR);
            uProf.setAtivo(true);
            uProf.setCriadoEm(Instant.now());
            uProf = userRepo.save(uProf);

            // Usuário Aluno
            User uAluno = new User();
            uAluno.setEmail("aluno@uni.x");
            uAluno.setSenhaHash("{noop}senha");
            uAluno.setRole(Role.ALUNO);
            uAluno.setAtivo(true);
            uAluno.setCriadoEm(Instant.now());
            uAluno = userRepo.save(uAluno);

            // Professor
            Professor prof = new Professor();
            prof.setUser(uProf);
            prof.setInstituicao(inst);
            prof.setNome("Prof. Ada");
            prof.setCpf("111.111.111-11");
            prof.setDepartamento("Computação");
            prof.setSaldo(0); // começa zerado
            prof = profRepo.save(prof);

            // Aluno
            Aluno aluno = new Aluno();
            aluno.setUser(uAluno);
            aluno.setInstituicao(inst);
            aluno.setCurso(cco);
            aluno.setNome("Ana");
            aluno.setCpf("222.222.222-22");
            aluno.setSaldo(0); // começa zerado
            aluno = alunoRepo.save(aluno);

            // Usuário Empresa
            User uEmp = new User();
            uEmp.setEmail("empresa@loja.x");
            uEmp.setSenhaHash("{noop}senha");
            uEmp.setRole(Role.EMPRESA);
            uEmp.setAtivo(true);
            uEmp.setCriadoEm(Instant.now());
            uEmp = userRepo.save(uEmp);

            // Empresa
            Empresa emp = new Empresa();
            emp.setUser(uEmp);
            emp.setCnpj("00.000.000/0001-00");
            emp.setNomeFantasia("Loja Parceira");
            emp.setEmailContato("contato@loja.x"); // ← email real para testar Mailjet
            emp.setEndereco("Av Central, 100");
            emp = empRepo.save(emp);

            // Vantagem
            Vantagem v = new Vantagem();
            v.setEmpresa(emp);
            v.setTitulo("Desconto 10% Lanchonete");
            v.setCusto(200);
            v.setAtivo(true);
            v = vantRepo.save(v);

            // === Crédito Semestral ===
            creditoSrv.creditar(prof.getId(), 2025, 2);
            // Agora o professor tem 1000 moedas (saldo inicial + crédito)

            // === Enviar moedas ===
            try {
                moedaSrv.enviarMoedas(prof.getId(), aluno.getId(), 300,
                        "Participação exemplar em aula");
            } catch (Exception e) {
                System.out.println("Falha ao enviar moedas: " + e.getMessage());
            }

            // === Resgatar Vantagem ===
            try {
                moedaSrv.resgatarVantagem(aluno.getId(), v.getId());
            } catch (Exception e) {
                System.out.println("Falha ao resgatar vantagem: " + e.getMessage());
            }

            System.out.println("==== BOOTSTRAP FINALIZADO ====");
        };
    }
}
