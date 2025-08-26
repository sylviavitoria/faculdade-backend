INSERT INTO tb_aluno (nome, email, matricula, senha)
SELECT 'Luana Silva', 'luanasilva@universidade.com', '2025001', '$2a$12$QwTgPuTNJwrlMf5wwhS/ZuMhFTAsTwKgd4UZsRcM6A9NufdoKKkLy'
WHERE NOT EXISTS (SELECT 1 FROM tb_aluno WHERE email = 'luanasilva@universidade.com');

INSERT INTO tb_usuario (email, senha, tipo, aluno_id, professor_id)
SELECT 'luanasilva@universidade.com', '$2a$12$QwTgPuTNJwrlMf5wwhS/ZuMhFTAsTwKgd4UZsRcM6A9NufdoKKkLy', 'ALUNO', a.id, NULL
FROM tb_aluno a
WHERE a.email = 'luanasilva@universidade.com'
AND NOT EXISTS (SELECT 1 FROM tb_usuario WHERE email = 'luanasilva@universidade.com');