package com.example.todoapp.graphql.config;

import com.example.todoapp.graphql.exception.GraphQLExceptionHandler;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.Date);
    }
    
    @Bean
    public GraphQLExceptionHandler graphQLExceptionHandler() {
        return new GraphQLExceptionHandler();
    }
}
