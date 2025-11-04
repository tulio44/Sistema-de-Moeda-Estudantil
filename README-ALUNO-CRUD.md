# Documentação - CRUD de Aluno

## Endpoints

### Criar Aluno
- **POST** `/alunos`
- **Body:** JSON do Aluno (incluindo user, instituicao, curso, nome, cpf, rg, endereco)
- **Resposta:** Aluno criado

### Listar Alunos
- **GET** `/alunos`
- **Resposta:** Lista de alunos

### Buscar Aluno por ID
- **GET** `/alunos/{id}`
- **Resposta:** Aluno correspondente ou 404

### Atualizar Aluno
- **PUT** `/alunos/{id}`
- **Body:** JSON do Aluno atualizado
- **Resposta:** Aluno atualizado ou 404

### Deletar Aluno
- **DELETE** `/alunos/{id}`
- **Resposta:** 204 No Content

## Regras de Negócio
- O campo `user` deve conter um usuário válido (com email e senha).
- O campo `instituicao` deve referenciar uma instituição já cadastrada.
- O campo `curso` deve referenciar um curso já cadastrado.
- CPF deve ser único.

## Exemplo de JSON para criação de Aluno
```json
{
  "nome": "João da Silva",
  "cpf": "12345678901",
  "rg": "1234567",
  "endereco": "Rua Exemplo, 123",
  "user": { "email": "joao@email.com", "senhaHash": "senha123", "role": "ALUNO" },
  "instituicao": { "id": 1 },
  "curso": { "id": 1 }
}
```

## Testes
- Os testes unitários do serviço de Aluno estão em `src/test/java/com/moedaestudantil/service/AlunoServiceTest.java`.
- Os testes cobrem: salvar, listar, buscar por id, atualizar e deletar.

## Observações
- Todos os endpoints retornam status HTTP apropriados (200, 201, 204, 404).
- Para produção, recomenda-se adicionar autenticação e validação de dados.
