package com.moedaestudantil.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GeradorCupomService {

    /**
     * Gera um código de cupom único, formatado como uma string de 12 caracteres em
     * maiúsculas.
     * 
     * @return O código de cupom gerado.
     */
    public String gerarCodigoCupom() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}