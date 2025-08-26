INSERT INTO tb_professor (nome, email, senha)
SELECT 'Lucas Silva', 'lucassilva@universidade.com', '$2a$12$YTAL7eUx4uWiV4F0bhtgNeS1MDmIb9cK2KzqFlDRCHs1z5bknXtia'
WHERE NOT EXISTS (SELECT 1 FROM tb_professor WHERE email = 'lucassilva@universidade.com');

INSERT INTO tb_usuario (email, senha, tipo, aluno_id, professor_id)
SELECT 'lucassilva@universidade.com', '$2a$12$YTAL7eUx4uWiV4F0bhtgNeS1MDmIb9cK2KzqFlDRCHs1z5bknXtia', 'PROFESSOR', NULL, p.id
FROM tb_professor p
WHERE p.email = 'lucassilva@universidade.com'
AND NOT EXISTS (SELECT 1 FROM tb_usuario WHERE email = 'lucassilva@universidade.com');