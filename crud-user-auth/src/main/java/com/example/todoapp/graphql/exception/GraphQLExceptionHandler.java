package com.example.todoapp.graphql.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GraphQLExceptionHandler implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        Map<String, Object> extensions = new HashMap<>();
        
        if (exception instanceof EntityNotFoundException) {
            extensions.put("code", "NOT_FOUND");
            extensions.put("classification", "DataFetchingException");
            
            GraphQLError error = GraphqlErrorBuilder.newError()
                    .message(exception.getMessage())
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .errorType(ErrorType.NOT_FOUND)
                    .extensions(extensions)
                    .build();
                    
            return Mono.just(Collections.singletonList(error));
        } else if (exception instanceof ConstraintViolationException) {
            extensions.put("code", "VALIDATION_ERROR");
            extensions.put("classification", "ValidationException");
            
            GraphQLError error = GraphqlErrorBuilder.newError()
                    .message("Erro de validação: " + exception.getMessage())
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .errorType(ErrorType.BAD_REQUEST)
                    .extensions(extensions)
                    .build();
                    
            return Mono.just(Collections.singletonList(error));
        } else {
            extensions.put("code", "INTERNAL_ERROR");
            extensions.put("classification", "InternalException");
            
            GraphQLError error = GraphqlErrorBuilder.newError()
                    .message("Erro interno: " + exception.getMessage())
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .extensions(extensions)
                    .build();
                    
            return Mono.just(Collections.singletonList(error));
        }
    }
}
