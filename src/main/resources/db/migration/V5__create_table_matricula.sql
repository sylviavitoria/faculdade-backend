CREATE TABLE tb_matricula (
    id SERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    disciplina_id BIGINT NOT NULL,
    nota1 DECIMAL(4,2),
    nota2 DECIMAL(4,2),
    media DECIMAL(4,2),
    status VARCHAR(20) DEFAULT 'CURSANDO',
    data_matricula TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_matricula_aluno FOREIGN KEY (aluno_id) REFERENCES tb_aluno(id),
    CONSTRAINT fk_matricula_disciplina FOREIGN KEY (disciplina_id) REFERENCES tb_disciplina(id),
    CONSTRAINT uk_matricula_aluno_disciplina UNIQUE (aluno_id, disciplina_id)
);