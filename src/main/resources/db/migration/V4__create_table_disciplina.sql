CREATE TABLE tb_disciplina (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    professor_id BIGINT,
    CONSTRAINT fk_disciplina_professor FOREIGN KEY (professor_id) REFERENCES tb_professor(id)
);
