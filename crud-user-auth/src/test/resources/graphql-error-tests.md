# Exemplo de Teste para Validação do Tratamento de Exceções GraphQL

## Cenário 1: Buscar tarefa com ID inexistente

```graphql
query {
  getTaskById(id: 999) {
    id
    title
    description
  }
}
```

Resposta esperada:
```json
{
  "errors": [
    {
      "message": "Tarefa não encontrada com o ID: 999",
      "locations": [
        {
          "line": 2,
          "column": 3
        }
      ],
      "path": ["getTaskById"],
      "extensions": {
        "code": "NOT_FOUND",
        "classification": "DataFetchingException"
      }
    }
  ],
  "data": null
}
```

## Cenário 2: Criar tarefa com dados inválidos

```graphql
mutation {
  createTask(task: {
    title: "",
    description: "Descrição da tarefa"
  }) {
    id
    title
  }
}
```

Resposta esperada:
```json
{
  "errors": [
    {
      "message": "Erro de validação: O título da tarefa é obrigatório",
      "locations": [
        {
          "line": 2,
          "column": 3
        }
      ],
      "path": ["createTask"],
      "extensions": {
        "code": "VALIDATION_ERROR",
        "classification": "ValidationException"
      }
    }
  ],
  "data": null
}
```

## Cenário 3: Erro interno do servidor

Se ocorrer um erro interno durante o processamento de uma consulta GraphQL, a resposta deve ser:

```json
{
  "errors": [
    {
      "message": "Erro interno: [mensagem específica do erro]",
      "locations": [...],
      "path": [...],
      "extensions": {
        "code": "INTERNAL_ERROR",
        "classification": "InternalException"
      }
    }
  ],
  "data": null
}
```

## Instruções para Validação

1. Execute a aplicação localmente com `mvn spring-boot:run`
2. Acesse a interface GraphiQL em `http://localhost:8080/graphiql`
3. Execute as consultas acima e verifique se as respostas de erro estão formatadas conforme esperado
4. Se as respostas não corresponderem ao formato esperado, verifique os logs do servidor para identificar possíveis problemas
