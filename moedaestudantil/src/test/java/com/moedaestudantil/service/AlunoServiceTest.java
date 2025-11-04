package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.model.Curso;
import com.moedaestudantil.domain.model.Instituicao;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.repo.AlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlunoServiceTest {
    private AlunoRepository alunoRepository;
    private AlunoService alunoService;

    @BeforeEach
    void setUp() {
        alunoRepository = Mockito.mock(AlunoRepository.class);
        alunoService = new AlunoService(alunoRepository);
    }

    @Test
    void testSalvar() {
        Aluno aluno = criarAluno();
        when(alunoRepository.save(aluno)).thenReturn(aluno);
        Aluno salvo = alunoService.salvar(aluno);
        assertEquals(aluno, salvo);
    }

    @Test
    void testListarTodos() {
        Aluno aluno1 = criarAluno();
        Aluno aluno2 = criarAluno();
        when(alunoRepository.findAll()).thenReturn(Arrays.asList(aluno1, aluno2));
        List<Aluno> lista = alunoService.listarTodos();
        assertEquals(2, lista.size());
    }

    @Test
    void testBuscarPorId() {
        Aluno aluno = criarAluno();
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        Optional<Aluno> resultado = alunoService.buscarPorId(1L);
        assertTrue(resultado.isPresent());
        assertEquals(aluno, resultado.get());
    }

    @Test
    void testAtualizar() {
        Aluno aluno = criarAluno();
        Aluno atualizado = criarAluno();
        atualizado.setNome("Novo Nome");
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(atualizado);
        Aluno result = alunoService.atualizar(1L, atualizado);
        assertEquals("Novo Nome", result.getNome());
    }

    @Test
    void testDeletar() {
        doNothing().when(alunoRepository).deleteById(1L);
        assertDoesNotThrow(() -> alunoService.deletar(1L));
    }

    private Aluno criarAluno() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno Teste");
        aluno.setCpf("12345678901");
        aluno.setRg("1234567");
        aluno.setEndereco("Rua Teste");
        aluno.setInstituicao(new Instituicao());
        aluno.setCurso(new Curso());
        aluno.setUser(new User());
        return aluno;
    }
}
