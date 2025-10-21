CREATE TABLE instituicao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(200) NOT NULL
);
CREATE TABLE curso (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  instituicao_id BIGINT NOT NULL,
  nome VARCHAR(200) NOT NULL,
  FOREIGN KEY (instituicao_id) REFERENCES instituicao(id)
);
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(180) NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL,
  role ENUM('ALUNO','PROFESSOR','EMPRESA') NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE aluno (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  instituicao_id BIGINT NOT NULL,
  curso_id BIGINT NOT NULL,
  nome VARCHAR(200) NOT NULL,
  cpf VARCHAR(14) NOT NULL UNIQUE,
  rg VARCHAR(30),
  endereco VARCHAR(255),
  saldo INT NOT NULL DEFAULT 0,
  versao BIGINT NOT NULL DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (instituicao_id) REFERENCES instituicao(id),
  FOREIGN KEY (curso_id) REFERENCES curso(id)
);
CREATE TABLE professor (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  instituicao_id BIGINT NOT NULL,
  nome VARCHAR(200) NOT NULL,
  cpf VARCHAR(14) NOT NULL UNIQUE,
  departamento VARCHAR(120) NOT NULL,
  saldo INT NOT NULL DEFAULT 0,
  versao BIGINT NOT NULL DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (instituicao_id) REFERENCES instituicao(id)
);
CREATE TABLE empresa (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  nome_fantasia VARCHAR(200) NOT NULL,
  cnpj VARCHAR(18) NOT NULL UNIQUE,
  email_contato VARCHAR(180),
  endereco VARCHAR(255),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE vantagem (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  empresa_id BIGINT NOT NULL,
  titulo VARCHAR(200) NOT NULL,
  descricao TEXT,
  foto_url VARCHAR(500),
  custo INT NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);
CREATE TABLE transacao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tipo ENUM('ENVIO_PROFESSOR','RESGATE_ALUNO','CREDITO_SEMESTRAL') NOT NULL,
  origem_professor_id BIGINT NULL,
  destino_aluno_id BIGINT NULL,
  vantagem_id BIGINT NULL,
  quantidade INT NOT NULL,
  mensagem VARCHAR(500),
  codigo_cupom VARCHAR(64),
  criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (origem_professor_id) REFERENCES professor(id),
  FOREIGN KEY (destino_aluno_id) REFERENCES aluno(id),
  FOREIGN KEY (vantagem_id) REFERENCES vantagem(id),
  CHECK (quantidade > 0)
);
CREATE TABLE credito_semestral (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  professor_id BIGINT NOT NULL,
  ano_ref INT NOT NULL,
  semestre_ref ENUM('1','2') NOT NULL,
  creditado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (professor_id, ano_ref, semestre_ref),
  FOREIGN KEY (professor_id) REFERENCES professor(id)
);
CREATE TABLE notificacao_email (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tipo ENUM('MOEDA_RECEBIDA','CUPOM_ALUNO','CUPOM_EMPRESA') NOT NULL,
  destinatario_email VARCHAR(180) NOT NULL,
  payload_json JSON,
  status ENUM('PENDENTE','ENVIADO','ERRO') NOT NULL DEFAULT 'PENDENTE',
  criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_tx_dest ON transacao(destino_aluno_id, criado_em);
CREATE INDEX idx_tx_prof ON transacao(origem_professor_id, criado_em);
