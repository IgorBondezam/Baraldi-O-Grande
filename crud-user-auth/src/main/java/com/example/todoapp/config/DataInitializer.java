package com.example.todoapp.config;

import com.example.todoapp.domain.enums.ERole;
import com.example.todoapp.domain.Role;
import com.example.todoapp.domain.Task;
import com.example.todoapp.domain.User;
import com.example.todoapp.domain.enums.TaskPriority;
import com.example.todoapp.repository.RoleRepository;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, 
                                     UserRepository userRepository,
                                     TaskRepository taskRepository,
                                     PasswordEncoder encoder) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role userRole = new Role();
                userRole.setName(ERole.ROLE_USER);
                roleRepository.save(userRole);
                
                Role modRole = new Role();
                modRole.setName(ERole.ROLE_MODERATOR);
                roleRepository.save(modRole);
                
                Role adminRole = new Role();
                adminRole.setName(ERole.ROLE_ADMIN);
                roleRepository.save(adminRole);
                
                System.out.println("Roles inicializadas com sucesso!");
            }
            
            if (!userRepository.existsByUsername("test")) {
                User testUser = new User();
                testUser.setUsername("test");
                testUser.setEmail("test@example.com");
                testUser.setPassword(encoder.encode("password"));
                
                Set<Role> roles = new HashSet<>();
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Role não encontrada"));
                roles.add(userRole);
                testUser.setRoles(roles);
                
                userRepository.save(testUser);
                System.out.println("Usuário de teste criado com sucesso!");
                
                Task task1 = new Task();
                task1.setTitle("Implementar autenticação JWT");
                task1.setDescription("Adicionar autenticação JWT ao projeto Spring Boot");
                task1.setCompleted(true);
                task1.setPriority(TaskPriority.HIGH);
                task1.setCreatedAt(LocalDateTime.now().minusDays(5));
                task1.setUpdatedAt(LocalDateTime.now().minusDays(2));
                task1.setUser(testUser);
                taskRepository.save(task1);
                
                Task task2 = new Task();
                task2.setTitle("Implementar GraphQL");
                task2.setDescription("Adicionar suporte a GraphQL ao projeto");
                task2.setCompleted(false);
                task2.setPriority(TaskPriority.MEDIUM);
                task2.setCreatedAt(LocalDateTime.now().minusDays(3));
                task2.setDueDate(LocalDateTime.now().plusDays(2));
                task2.setUser(testUser);
                taskRepository.save(task2);
                
                Task task3 = new Task();
                task3.setTitle("Escrever documentação");
                task3.setDescription("Documentar a API REST e GraphQL");
                task3.setCompleted(false);
                task3.setPriority(TaskPriority.LOW);
                task3.setCreatedAt(LocalDateTime.now().minusDays(1));
                task3.setDueDate(LocalDateTime.now().plusDays(5));
                task3.setUser(testUser);
                taskRepository.save(task3);
                
                System.out.println("Tarefas de exemplo criadas com sucesso!");
            }
        };
    }
}
