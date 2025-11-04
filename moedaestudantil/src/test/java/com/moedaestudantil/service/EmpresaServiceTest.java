package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.Empresa;
import com.moedaestudantil.domain.model.User;
import com.moedaestudantil.domain.repo.EmpresaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmpresaServiceTest {
    private EmpresaRepository empresaRepository;
    private EmpresaService empresaService;

    @BeforeEach
    void setUp() {
        empresaRepository = Mockito.mock(EmpresaRepository.class);
        empresaService = new EmpresaService(empresaRepository);
    }

    @Test
    void testSalvar() {
        Empresa empresa = criarEmpresa();
        when(empresaRepository.save(empresa)).thenReturn(empresa);
        Empresa salva = empresaService.salvar(empresa);
        assertEquals(empresa, salva);
    }

    @Test
    void testListarTodas() {
        Empresa empresa1 = criarEmpresa();
        Empresa empresa2 = criarEmpresa();
        when(empresaRepository.findAll()).thenReturn(Arrays.asList(empresa1, empresa2));
        List<Empresa> lista = empresaService.listarTodas();
        assertEquals(2, lista.size());
    }

    @Test
    void testBuscarPorId() {
        Empresa empresa = criarEmpresa();
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        Optional<Empresa> resultado = empresaService.buscarPorId(1L);
        assertTrue(resultado.isPresent());
        assertEquals(empresa, resultado.get());
    }

    @Test
    void testAtualizar() {
        Empresa empresa = criarEmpresa();
        Empresa atualizada = criarEmpresa();
        atualizada.setNomeFantasia("Nova Empresa");
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.save(any(Empresa.class))).thenReturn(atualizada);
        Empresa result = empresaService.atualizar(1L, atualizada);
        assertEquals("Nova Empresa", result.getNomeFantasia());
    }

    @Test
    void testDeletar() {
        doNothing().when(empresaRepository).deleteById(1L);
        assertDoesNotThrow(() -> empresaService.deletar(1L));
    }

    private Empresa criarEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNomeFantasia("Empresa Teste");
        empresa.setCnpj("12345678000199");
        empresa.setEmailContato("contato@empresa.com");
        empresa.setEndereco("Rua Empresa, 100");
        empresa.setUser(new User());
        return empresa;
    }
}
