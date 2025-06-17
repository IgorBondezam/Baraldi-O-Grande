# Exemplos de Testes para Validação de Endpoints REST e GraphQL

## Testes REST (usando curl ou Postman)

### 1. Listar todas as tarefas
```bash
curl -X GET http://localhost:8080/api/tasks
```

### 2. Obter tarefa por ID
```bash
curl -X GET http://localhost:8080/api/tasks/1
```

### 3. Criar nova tarefa
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implementar GraphQL",
    "description": "Adicionar suporte a GraphQL na aplicação",
    "priority": "HIGH",
    "dueDate": "2023-06-30T18:00:00"
  }'
```

### 4. Atualizar tarefa existente
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implementar GraphQL e DTOs",
    "description": "Adicionar suporte a GraphQL e DTOs na aplicação",
    "completed": false,
    "priority": "HIGH",
    "dueDate": "2023-06-30T18:00:00"
  }'
```

### 5. Alternar status de conclusão
```bash
curl -X PATCH http://localhost:8080/api/tasks/1/toggle
```

### 6. Excluir tarefa
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

### 7. Filtrar por status
```bash
curl -X GET http://localhost:8080/api/tasks/status?completed=false
```

### 8. Filtrar por prioridade
```bash
curl -X GET http://localhost:8080/api/tasks/priority/HIGH
```

### 9. Listar tarefas atrasadas
```bash
curl -X GET http://localhost:8080/api/tasks/overdue
```

### 10. Listar tarefas pendentes ordenadas
```bash
curl -X GET http://localhost:8080/api/tasks/pending
```

### 11. Buscar por título
```bash
curl -X GET http://localhost:8080/api/tasks/search?title=GraphQL
```

## Testes GraphQL (usando GraphiQL ou Postman)

### 1. Listar todas as tarefas
```graphql
query {
  getAllTasks {
    id
    title
    description
    completed
    createdAt
    updatedAt
    dueDate
    priority
  }
}
```

### 2. Obter tarefa por ID
```graphql
query {
  getTaskById(id: 1) {
    id
    title
    description
    completed
    createdAt
    updatedAt
    dueDate
    priority
  }
}
```

### 3. Criar nova tarefa
```graphql
mutation {
  createTask(task: {
    title: "Testar GraphQL",
    description: "Validar queries e mutations GraphQL",
    completed: false,
    dueDate: "2023-07-15T12:00:00",
    priority: HIGH
  }) {
    id
    title
    description
    completed
    createdAt
    dueDate
    priority
  }
}
```

### 4. Atualizar tarefa existente
```graphql
mutation {
  updateTask(
    id: 1,
    task: {
      title: "Testar GraphQL e DTOs",
      description: "Validar queries, mutations e DTOs",
      completed: true,
      priority: HIGH
    }
  ) {
    id
    title
    description
    completed
    updatedAt
    priority
  }
}
```

### 5. Alternar status de conclusão
```graphql
mutation {
  toggleTaskCompletion(id: 1) {
    id
    title
    completed
    updatedAt
  }
}
```

### 6. Excluir tarefa
```graphql
mutation {
  deleteTask(id: 1)
}
```

### 7. Filtrar por status
```graphql
query {
  getTasksByStatus(completed: false) {
    id
    title
    completed
    priority
  }
}
```

### 8. Filtrar por prioridade
```graphql
query {
  getTasksByPriority(priority: HIGH) {
    id
    title
    priority
    dueDate
  }
}
```

### 9. Listar tarefas atrasadas
```graphql
query {
  getOverdueTasks {
    id
    title
    dueDate
    priority
  }
}
```

### 10. Listar tarefas pendentes ordenadas
```graphql
query {
  getPendingTasks {
    id
    title
    priority
    dueDate
  }
}
```

### 11. Buscar por título
```graphql
query {
  searchTasksByTitle(title: "GraphQL") {
    id
    title
    description
  }
}
```

## Testes de Tratamento de Erros

### REST - Tarefa não encontrada
```bash
curl -X GET http://localhost:8080/api/tasks/999
```

### GraphQL - Tarefa não encontrada
```graphql
query {
  getTaskById(id: 999) {
    id
    title
  }
}
```

### REST - Validação de dados
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "",
    "description": "Descrição sem título"
  }'
```

### GraphQL - Validação de dados
```graphql
mutation {
  createTask(task: {
    title: "",
    description: "Descrição sem título"
  }) {
    id
    title
  }
}
```
