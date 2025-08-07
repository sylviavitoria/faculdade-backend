CREATE TABLE tb_usuario (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    aluno_id BIGINT,
    professor_id BIGINT,
    CONSTRAINT fk_usuario_aluno FOREIGN KEY (aluno_id) REFERENCES tb_aluno(id),
    CONSTRAINT fk_usuario_professor FOREIGN KEY (professor_id) REFERENCES tb_professor(id)
);