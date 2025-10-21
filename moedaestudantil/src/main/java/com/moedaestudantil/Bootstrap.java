package com.moedaestudantil;

import com.moedaestudantil.domain.model.*;
import com.moedaestudantil.domain.model.enums.Role;
import com.moedaestudantil.domain.repo.*;
import com.moedaestudantil.service.CreditoSemestralService;
import com.moedaestudantil.service.MoedaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

      Instituicao inst = new Instituicao();
      inst.setNome("Universidade X");
      inst = instRepo.save(inst);

      Curso cco = new Curso();
      cco.setInstituicao(inst);
      cco.setNome("Computação");
      cco = cursoRepo.save(cco);

      User uProf = new User();
      uProf.setEmail("prof@uni.x");
      uProf.setSenhaHash("{noop}senha");
      uProf.setRole(Role.PROFESSOR);
      uProf = userRepo.save(uProf);

      User uAluno = new User();
      uAluno.setEmail("aluno@uni.x");
      uAluno.setSenhaHash("{noop}senha");
      uAluno.setRole(Role.ALUNO);
      uAluno = userRepo.save(uAluno);

      Professor prof = new Professor();
      prof.setUser(uProf);
      prof.setInstituicao(inst);
      prof.setNome("Prof. Ada");
      prof.setCpf("111.111.111-11");
      prof.setDepartamento("Computação");
      prof.setSaldo(0);
      prof = profRepo.save(prof);

      Aluno aluno = new Aluno();
      aluno.setUser(uAluno);
      aluno.setInstituicao(inst);
      aluno.setCurso(cco);
      aluno.setNome("Ana");
      aluno.setCpf("222.222.222-22");
      aluno.setSaldo(0);
      aluno = alunoRepo.save(aluno);

      User uEmp = new User();
      uEmp.setEmail("empresa@loja.x");
      uEmp.setSenhaHash("{noop}senha");
      uEmp.setRole(Role.EMPRESA);
      uEmp = userRepo.save(uEmp);

      Empresa emp = new Empresa();
      emp.setUser(uEmp);
      emp.setNomeFantasia("Loja Parceira");
      emp.setEmailContato("contato@loja.x");
      emp = empRepo.save(emp);

      Vantagem v = new Vantagem();
      v.setEmpresa(emp);
      v.setTitulo("Desconto 10% Lanchonete");
      v.setCusto(200);
      v.setAtivo(true);
      v = vantRepo.save(v);

      creditoSrv.creditar(prof.getId(), 2025, 2);
      moedaSrv.enviarMoedas(prof.getId(), aluno.getId(), 150, "Participação em aula");
      moedaSrv.resgatarVantagem(aluno.getId(), v.getId());

      System.out.println("==== BOOTSTRAP FINALIZADO ====");
    };
  }
}
