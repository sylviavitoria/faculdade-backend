INSERT INTO tb_usuario (email, senha, tipo, aluno_id, professor_id)
SELECT 'admin@exemplo.com', '$2a$12$47B4VBERrBiUp2cFSvafS.TJbQ9QaAAj6jh4BSIoeohK2lYA.F6v2', 'ADMIN', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM tb_usuario WHERE email = 'admin@exemplo.com');