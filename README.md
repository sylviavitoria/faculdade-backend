# 🎓 Sistema de Gerenciamento Acadêmico - API REST com Spring Boot

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-6DB33F?style=flat&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-316192?style=flat&logo=postgresql&logoColor=white)](https://postgresql.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-47A248?style=flat&logo=mongodb&logoColor=white)](https://mongodb.com/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat&logo=docker&logoColor=white)](https://docker.com/)

Uma **API RESTful completa** para gerenciamento de sistemas acadêmicos, desenvolvida com Spring Boot. O sistema permite o gerenciamento de **alunos**, **professores**, **disciplinas** e **matrículas**, com autenticação JWT e sistema de logs robusto.

---

## 📌 **Objetivo**

Criar uma aplicação completa para gerenciar um sistema acadêmico com:
- **Gestão de Usuários** (Alunos, Professores, Administradores)
- **Matrícula de Alunos** em disciplinas
- **Lançamento e Gestão de Notas** por professores
- **Autenticação e Autorização** via JWT
- **Sistema de Logs** centralizado com MongoDB
- **Documentação Interativa** com Swagger

---

## 🏗️ **Arquitetura do Sistema**

### **Stack Tecnológica**
- **Backend**: Java 21 + Spring Boot 3.5.4
- Spring Data JPA
- **Segurança**: Spring Security + JWT
- **Banco Relacional**: PostgreSQL 15
- **Banco NoSQL**: MongoDB 7.0 (para logs)
- **Documentação**: Swagger/OpenAPI 3
- **Containerização**: Docker + Docker Compose
- **Build**: Maven
- Docker
- Lombok
- **Mapeamento**: MapStruct
- **Migrations**: Flyway
  
---
  
### **📦 Tecnologias utilizadas nos testes**
- **JUnit 5** - Framework de testes principal
- **Mockito** - Framework para criação de mocks e stubs
- **Spring Boot Test** - Testes de integração e slices
- **MockMvc** - Testes de endpoints REST sem servidor 
- **Spring Security Test** - Testes de segurança e autenticação
> Foram desenvolvidos testes unitários e de integração cobrindo models, services e controllers da aplicação.

---

## 📁 **Estrutura do Projeto**

```
src/
├── main/
│   ├── java/com/sylviavitoria/apifaculdade/
│   │   ├── config/          # Configurações (Security, Swagger)
│   │   ├── controller/      # Controllers REST
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── enums/          # Enumerações
│   │   ├── exception/      # Tratamento de exceções
│   │   ├── interfaces/     # Interfaces de serviços
│   │   ├── mapper/         # Mapeadores MapStruct
│   │   ├── model/          # Entidades JPA/MongoDB
│   │   ├── repository/     # Repositórios
│   │   ├── security/       # Configurações de segurança
│   │   └── service/        # Lógica de negócio
│   └── resources/
│       ├── application.properties
│       ├── application-h2.properties
│       ├── application-postgres.properties
│       └── db/migration/   # Scripts Flyway
└── test/                   # Testes unitários e integração
```

---

## 📚 **Regras de Negócio**

### **Alunos**
- ✅ Um aluno pode se matricular em **várias disciplinas**
- ✅ Cada aluno possui **matrícula única**
- ✅ Pode visualizar suas **disciplinas** e **notas**
- ✅ Acesso restrito aos **próprios dados**

### **Professores**
- ✅ Um professor pode ministrar **várias disciplinas**
- ✅ Pode **lançar e editar notas** apenas das suas disciplinas
- ✅ Visualiza **alunos matriculados** nas suas disciplinas
- ✅ Acesso aos dados dos alunos das suas turmas

### **Disciplinas e Matrículas**
- ✅ Cada disciplina tem **um professor responsável**
- ✅ Um aluno pode ter **apenas uma matrícula por disciplina**
- ✅ Sistema de **duas notas** com **cálculo automático de média**
- ✅ **Status da matrícula** (Cursando, Aprovado, Reprovado)

### **Administradores**
- ✅ **Gestão completa** de alunos, professores e disciplinas
- ✅ **Matricular alunos** em disciplinas
- ✅ **Atribuir professores** às disciplinas
- ✅ **Visualização de logs** do sistema
- ✅ **Criação de novos administradores**

---

## 🚀 **Funcionalidades por Perfil**

### 👨‍🎓 **Aluno**
- **Login** no sistema
- **Consultar dados pessoais** (`/api/v1/alunos/me`)
- **Visualizar disciplinas matriculadas**
- **Consultar notas e média**

### 👩‍🏫 **Professor**
- **Login** no sistema
- **Consultar dados pessoais** (`/api/v1/professores/me`)
- **Visualizar disciplinas** que ministra
- **Ver alunos matriculados** em cada disciplina
- **Lançar e editar notas** dos alunos
- **Consultar matrículas** das suas disciplinas

### 🛠️ **Administrador**
- **Login** no sistema
- **CRUD completo** de:
  - Alunos
  - Professores
  - Disciplinas
  - Usuários administradores
- **Gestão de matrículas**:
  - Matricular alunos
  - Cancelar matrículas
- **Atribuir professores** às disciplinas
- **Consultar logs** do sistema
- **Relatórios** e consultas avançadas

---

## 🔐 **Autenticação e Autorização**

### **Sistema JWT**
- **Token de acesso** com expiração configurável (24h padrão)
- **Autorização baseada em roles**: `ROLE_ADMIN`, `ROLE_PROFESSOR`, `ROLE_ALUNO`
- **Endpoints protegidos** por perfil de usuário

### **Segurança Implementada**
- **Senhas criptografadas** com BCrypt
- **Headers de segurança** configurados
- **CORS** configurado para desenvolvimento
- **Proteção CSRF** desabilitada para API REST

---

## 📋 **Documentação da API**

### **Endpoints Principais**

#### **🔑 Autenticação**
```http
POST /api/v1/auth/login

{
  "email": "admin@exemplo.com",
  "senha": "123"
}
```

#### **👨‍🎓 Gestão de Alunos**
```http
POST   /api/v1/alunos           # Criar aluno (Admin)
{
  "nome": "João da Silva",
  "email": "joao.silva@email.com",
  "matricula": "2023001234",
  "senha": "123"
}
```
```http
GET    /api/v1/alunos           # Listar alunos (Admin/Professor)
GET    /api/v1/alunos/{id}      # Buscar aluno (Admin/Professor)
DELETE /api/v1/alunos/{id}      # Deletar aluno (Admin)
GET    /api/v1/alunos/me        # Dados do aluno logado (Aluno)
```
```http
PUT    /api/v1/alunos/{id}      # Atualizar aluno (Admin)
{
  "nome": "João da Silva",
  "email": "joao.silva@email.com",
  "matricula": "2023001234",
  "senha": "123"
}
```

#### **👩‍🏫 Gestão de Professores**
```http
POST   /api/v1/professores      # Criar professor (Admin)
{
  "nome": "Maria Silva",
  "email": "maria.silva@universidade.com",
  "senha": "123"
}
```
```http
GET    /api/v1/professores      # Listar professores (Admin)
GET    /api/v1/professores/{id} # Buscar professor (Admin)
DELETE /api/v1/professores/{id} # Deletar professor (Admin)
GET    /api/v1/professores/me   # Dados do professor logado (Professor)
```
```http
PUT    /api/v1/professores/{id} # Atualizar professor (Admin)
{
  "nome": "Maria Silva",
  "email": "maria.silva@universidade.com",
  "senha": "123"
}
```

#### **📚 Gestão de Disciplinas**
```http
POST   /api/v1/disciplinas      # Criar disciplina (Admin)
{
  "nome": "Algoritmos",
  "codigo": "ALG101",
  "professorId": 1
}
```
```http
GET    /api/v1/disciplinas      # Listar disciplinas
GET    /api/v1/disciplinas/{id} # Buscar disciplina
DELETE /api/v1/disciplinas/{id} # Deletar disciplina (Admin)
```
```http
PUT    /api/v1/disciplinas/{id} # Atualizar disciplina (Admin)
{
  "nome": "Algoritmos",
  "codigo": "ALG101",
  "professorId": 1
}
```

#### **📝 Gestão de Matrículas**
```http
POST   /api/v1/matriculas       # Criar matrícula (Admin)
{
  "alunoId": 1,
  "disciplinaId": 1
}
```
```http
GET    /api/v1/matriculas       # Listar matrículas (Admin)
GET    /api/v1/matriculas/{id}  # Buscar matrícula
DELETE /api/v1/matriculas/{id}  # Deletar matrícula (Admin)
```
```http
PUT    /api/v1/matriculas/{id}/notas # Atualizar notas (Admin/Professor)
{
  "nota1": 8.5,
  "nota2": 7.0
}
```

#### **🧑‍🤝‍🧑 Gestão de Usuários**
```http
POST   /api/v1/usuarios/admins  # Criar administrador (Admin)
{
  "email": "admin@exemplo.com",
  "senha": "123",
  "tipo": "ADMIN"
}
```

#### **📊 Logs do Sistema**
```http
GET /api/v1/logs          # Todos os logs (Admin)
GET /api/v1/logs/recentes # Logs das últimas 24h (Admin)
```

### **Swagger UI**
Acesse a documentação interativa em: **http://localhost:8080/swagger-ui.html**

---

## 📋 **Dados Iniciais**

O sistema já vem com alguns dados iniciais configurados via Flyway:

### **Usuário Administrador Padrão**
```json
{
  "email": "admin@exemplo.com",
  "senha": "123"
}
```
> Então você já pode realizar o login diretamente desse usuário padrão e obter o token, para facilitar os testes da aplicação.
---

## 📊 **Sistema de Logs**

### **Funcionalidades de Log**
- **Logs estruturados** em MongoDB
- **Rastreamento de operações** CRUD
- **Informações de usuário** logado
- **Timestamps** e **níveis** de log
- **Consulta de logs** via API

### **Exemplo de Log**
```json
{
  "timestamp": "2025-08-08T10:30:00",
  "level": "INFO",
  "message": "Aluno criado com sucesso: João Silva (ID: 1)",
  "className": "AlunoServiceImpl",
  "method": "criarAluno",
  "userId": "admin@exemplo.com",
  "operation": "CREATE_ALUNO"
}
```

---

## 🛠️ **Configuração e Execução**

### **Pré-requisitos**
- **Java 21+**
- **Docker** e **Docker Compose**
- **Maven 3.9+** (ou use o wrapper incluído)
- **Git**

### **1. Clone o repositório**
```bash
git clone https://github.com/sylviavitoria/faculdade-backend.git
cd faculdade-backend
```

### **2. Configuração do arquivo `.env`**
Para facilitar a configuração do banco de dados e evitar informações sensíveis no código, crie um arquivo `.env` na raiz do projeto com o seguinte conteúdo:

```bash
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=apifaculdade
POSTGRES_PORT=5432

SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/${POSTGRES_DB}
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

PGADMIN_DEFAULT_EMAIL=admin@example.com
PGADMIN_DEFAULT_PASSWORD=admin
PGADMIN_PORT=5050

MONGO_ROOT_USERNAME=admin
MONGO_ROOT_PASSWORD=admin123
MONGOEXPRESS_LOGIN=admin
MONGOEXPRESS_PASSWORD=pass

SPRING_DATA_MONGODB_URI=mongodb://admin:admin123@mongodb:27017/faculdade_logs?authSource=admin&connectTimeoutMS=20000&socketTimeoutMS=20000&serverSelectionTimeoutMS=20000
```

> **📝 Observação:** 
> - O Docker Compose utiliza essas variáveis para subir os containers do PostgreSQL e MongoDB com as credenciais corretas.
> - O Spring Boot também lerá essas variáveis ao inicializar, garantindo que a conexão com o banco seja automática conforme o ambiente.

### **3. Execute a aplicação**
```bash
docker compose up --build
```

### **4. Serviços Disponíveis**
- **🚀 API Principal**: http://localhost:8080
- **📚 Swagger Documentation**: http://localhost:8080/swagger-ui.html
- **🐘 PostgreSQL**: localhost:5432
- **🔧 PgAdmin**: http://localhost:5050
- **🌿 Mongo Express**: http://localhost:8081

---

## 🔧 **Acesso aos Serviços**

### **📚 Swagger UI (Documentação da API)**
1. Acesse: http://localhost:8080/swagger-ui.html
2. Explore todos os endpoints disponíveis
3. Teste diretamente na interface interativa

### **🌿 Mongo Express para vizualizar logs (Gerenciador MongoDB)**
1. Acesse: http://localhost:8081
2. **Login:**
   - Username: `admin`
   - Password: `pass`
3. **Visualizar Logs:**
   - Database: `faculdade_logs`
   - Collection: `applicationLog`
   - Aqui você verá todos os logs estruturados da aplicação

---

### **🧪 Testando a API**

#### 📚 **Via Swagger**  **http://localhost:8080/swagger-ui.html**
Para maior simplicidade na autenticação, recomenda-se testar a API diretamente pelo **Swagger**.  

1. **Fazer Login**  
2. **Usar o Token:**  
   - Copie o token da resposta  
   - Clique no ícone de **cadeado** acima dos endpoints e cole o token  
   - Após autenticar, você poderá acessar todos os endpoints disponíveis  

---

#### 👨‍🔧 **Via Postman/Insomnia**  
1. **Fazer Login:**  
```http
POST http://localhost:8080/api/v1/auth/login

{
  "email": "admin@exemplo.com",
  "senha": "123"
}
```

2. **Usar o Token:**  
   - Copie o token da resposta  
   - Adicione no header:  
```
Authorization: Bearer Token{seu-token}
```

3. **Exemplo para testar Endpoints:**  
```http
GET http://localhost:8080/api/v1/alunos
Authorization: Bearer Token {seu-token}
```










