# Documentação - CRUD de Empresa Parceira

## Endpoints

### Criar Empresa
- **POST** `/empresas`
- **Body:** JSON da Empresa (incluindo user, cnpj, nomeFantasia, emailContato, endereco)
- **Resposta:** Empresa criada

### Listar Empresas
- **GET** `/empresas`
- **Resposta:** Lista de empresas

### Buscar Empresa por ID
- **GET** `/empresas/{id}`
- **Resposta:** Empresa correspondente ou 404

### Atualizar Empresa
- **PUT** `/empresas/{id}`
- **Body:** JSON da Empresa atualizada
- **Resposta:** Empresa atualizada ou 404

### Deletar Empresa
- **DELETE** `/empresas/{id}`
- **Resposta:** 204 No Content

## Regras de Negócio
- O campo `user` deve conter um usuário válido (com email e senha).
- O campo `cnpj` deve ser único.
- O campo `nomeFantasia` é obrigatório.

## Exemplo de JSON para criação de Empresa
```json
{
  "nomeFantasia": "Empresa Exemplo",
  "cnpj": "12345678000199",
  "emailContato": "contato@empresa.com",
  "endereco": "Rua Exemplo, 100",
  "user": { "email": "empresa@email.com", "senhaHash": "senha123", "role": "EMPRESA" }
}
```

## Testes
- Os testes unitários do serviço de Empresa estão em `src/test/java/com/moedaestudantil/service/EmpresaServiceTest.java`.
- Os testes cobrem: salvar, listar, buscar por id, atualizar e deletar.

## Observações
- Todos os endpoints retornam status HTTP apropriados (200, 201, 204, 404).
- Para produção, recomenda-se adicionar autenticação e validação de dados.
