# 🎓 Desafio Técnico — Sistema de Faculdade 

## 📌 Objetivo

Criar uma **API RESTful** para gerenciar um sistema acadêmico com **alunos, professores e disciplinas**.  
A aplicação deverá permitir **matrícula de alunos**, **lançamento de notas por professores** e **visualização dessas informações** por meio da API.  
A **autenticação será feita via JWT**.

---

## 📚 Regras

- Um aluno pode se matricular em várias disciplinas  
- Um professor pode ministrar várias disciplinas  
- Um aluno só pode ter **uma nota por disciplina**  
- Apenas o **professor responsável** pode lançar notas para sua disciplina  
- A autenticação e autorização devem ser feitas com **JWT**

---

## ✅ Funcionalidades

### 👨‍🎓 Aluno pode:
- Fazer login  
- Ver suas disciplinas matriculadas  
- Ver suas notas

### 👩‍🏫 Professor pode:
- Fazer login  
- Ver as disciplinas que ministra  
- Ver os alunos de cada disciplina  
- Lançar ou editar nota dos alunos

### 🛠️ Admin pode:
- Fazer login  
- Cadastrar, editar e remover:
  - Alunos  
  - Professores  
  - Disciplinas  
- Matricular alunos nas disciplinas  
- Atribuir professores às disciplinas

---

## 🧩 Entidades

### `Aluno`
- `id`
- `nome`
- `email`
- `matricula`
- `senha`

### `Professor`
- `id`
- `nome`
- `email`
- `senha`

### `Disciplina`
- `id`
- `nome`
- `codigo`
- `professor_id`

### `Matrícula`
- `id`
- `aluno_id`
- `disciplina_id`
- `nota`

### `Usuário` (genérico para login)
- `email`
- `senha`
- `tipo` (`ADMIN`, `ALUNO`, `PROFESSOR`)

---

## 🛠️ Tecnologias obrigatórias

- Java com Spring Boot  
- Spring Security com JWT  
- Banco de dados relacional (**PostgreSQL**, **MySQL** ou **H2**)  
- Toda comunicação deve ser feita via **JSON**  
- Dados devem ser **persistidos** (não podem se perder ao reiniciar)  
- Documentação da API com **Swagger**  
- Banco NoSQL (**MongoDB**) para **logs**
