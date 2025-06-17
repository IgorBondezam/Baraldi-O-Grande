# ENTREGA
- Daniel Bonam Rissardi 22013838-2
- Matheus Zauza Maschietto 22013969-2
- Kauan Muriel Rossi - 22014501-2
- Matheus Baraldi - 22158952-2
- Igor bondezam França - 22012574-2

# Documentação da API To-Do List com Autenticação JWT

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.3
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- Spring GraphQL
- H2 Database (banco de dados em memória)
- Lombok
- Maven

## Visão Geral

Esta aplicação é uma API de gerenciamento de tarefas (To-Do List) com autenticação JWT, oferecendo endpoints REST e GraphQL. Cada usuário tem acesso apenas às suas próprias tarefas, garantindo segurança e isolamento de dados.

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.6 ou superior

## Configuração e Execução

1. Clone o repositório
2. Configure as propriedades JWT no arquivo `application.properties`
3. Execute o comando: `mvn spring-boot:run`
4. A aplicação estará disponível em `http://localhost:8080`

## Autenticação e Segurança

A aplicação utiliza JWT (JSON Web Token) para autenticação stateless. Todas as operações de tarefas exigem autenticação.

### Endpoints de Autenticação REST

#### Registro de Usuário
```
POST /api/auth/signup
```
Corpo da requisição:
```json
{
  "username": "usuario",
  "email": "usuario@example.com",
  "password": "senha123",
  "role": ["user"] // Opcional: "user", "mod", "admin"
}
```

#### Login
```
POST /api/auth/signin
```
Corpo da requisição:
```json
{
  "username": "usuario",
  "password": "senha123"
}
```
Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "usuario",
  "email": "usuario@example.com",
  "roles": ["ROLE_USER"]
}
```

### Autenticação GraphQL

#### Registro de Usuário
```graphql
mutation {
  register(
    username: "usuario",
    email: "usuario@example.com",
    password: "senha123",
    roles: ["user"]
  ) {
    message
  }
}
```

#### Login
```graphql
mutation {
  login(username: "usuario", password: "senha123") {
    token
    type
    id
    username
    email
    roles
  }
}
```

### Uso do Token JWT

Para todas as requisições protegidas, inclua o token JWT no cabeçalho:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Endpoints REST

Todos os endpoints abaixo exigem autenticação JWT.

### Tarefas

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | /api/tasks | Listar todas as tarefas do usuário autenticado |
| GET | /api/tasks/{id} | Obter tarefa por ID (se pertencer ao usuário) |
| POST | /api/tasks | Criar nova tarefa (associada ao usuário autenticado) |
| PUT | /api/tasks/{id} | Atualizar tarefa existente |
| DELETE | /api/tasks/{id} | Excluir tarefa |
| PATCH | /api/tasks/{id}/toggle | Alternar status de conclusão |
| GET | /api/tasks/status?completed=true | Filtrar por status |
| GET | /api/tasks/priority/{priority} | Filtrar por prioridade |
| GET | /api/tasks/overdue | Listar tarefas atrasadas |
| GET | /api/tasks/pending | Listar tarefas pendentes ordenadas |
| GET | /api/tasks/search?title=texto | Buscar por título |

### Exemplo de Criação de Tarefa

```
POST /api/tasks
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
Corpo:
```json
{
  "title": "Completar projeto",
  "description": "Finalizar implementação do projeto Spring Boot",
  "priority": "HIGH",
  "dueDate": "2025-06-20T18:00:00"
}
```

## API GraphQL

A API GraphQL está disponível em `/graphql` e também exige autenticação JWT.

### Queries

```graphql
# Obter todas as tarefas do usuário autenticado
query {
  getAllTasks {
    id
    title
    description
    completed
    priority
    dueDate
  }
}

# Obter tarefa por ID
query {
  getTaskById(id: 1) {
    id
    title
    description
    completed
  }
}

# Filtrar tarefas por status
query {
  getTasksByStatus(completed: false) {
    id
    title
    priority
  }
}

# Filtrar tarefas por prioridade
query {
  getTasksByPriority(priority: HIGH) {
    id
    title
    dueDate
  }
}

# Obter tarefas atrasadas
query {
  getOverdueTasks {
    id
    title
    dueDate
  }
}

# Obter tarefas pendentes ordenadas
query {
  getPendingTasks {
    id
    title
    priority
    dueDate
  }
}

# Buscar tarefas por título
query {
  searchTasks(title: "projeto") {
    id
    title
  }
}
```

### Mutations

```graphql
# Criar nova tarefa
mutation {
  createTask(input: {
    title: "Completar projeto",
    description: "Finalizar implementação do projeto Spring Boot",
    priority: HIGH,
    dueDate: "2025-06-20T18:00:00"
  }) {
    id
    title
    description
  }
}

# Atualizar tarefa
mutation {
  updateTask(id: 1, input: {
    title: "Projeto atualizado",
    description: "Nova descrição",
    priority: MEDIUM
  }) {
    id
    title
    description
    priority
  }
}

# Alternar status de conclusão
mutation {
  toggleTaskCompletion(id: 1) {
    id
    title
    completed
  }
}

# Excluir tarefa
mutation {
  deleteTask(id: 1)
}
```

## Modelo de Dados

### User
- id: Long
- username: String
- email: String
- password: String
- roles: Set<Role>
- tasks: List<Task>

### Role
- id: Integer
- name: ERole (ROLE_USER, ROLE_MODERATOR, ROLE_ADMIN)

### Task
- id: Long
- title: String
- description: String
- completed: boolean
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- dueDate: LocalDateTime
- priority: TaskPriority (LOW, MEDIUM, HIGH)
- user: User

## Segurança e Boas Práticas

- Autenticação stateless com JWT
- Senhas criptografadas com BCrypt
- Validação de propriedade de tarefas
- Proteção contra CSRF desativada (stateless)
- Controle de acesso baseado em usuário

## Inicialização de Dados

Para facilitar os testes, a aplicação inicializa automaticamente:
- Roles padrão (USER, MODERATOR, ADMIN)
- Um usuário de teste (username: "test", password: "password")
- Algumas tarefas de exemplo associadas ao usuário de teste
