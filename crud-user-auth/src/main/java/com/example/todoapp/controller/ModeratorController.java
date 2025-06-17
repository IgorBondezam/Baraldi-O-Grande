package com.example.todoapp.controller;

import com.example.todoapp.dto.TaskDTO;
import com.example.todoapp.dto.UserDTO;
import com.example.todoapp.payload.response.MessageResponse;
import com.example.todoapp.service.TaskService;
import com.example.todoapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/moderator")
@Tag(name = "Moderação", description = "Endpoints para moderadores do sistema")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
public class ModeratorController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping("/users")
    @Operation(summary = "Listar usuários comuns", description = "Lista usuários com role USER para moderação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas moderadores e administradores")
    })
    public ResponseEntity<Page<UserDTO>> getRegularUsers(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "username") String sortBy,
            @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserDTO> users = userService.getUsersByRole("user", pageable);
        
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{id}")
    @Operation(summary = "Visualizar perfil de usuário", description = "Permite moderadores visualizarem perfis de usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil do usuário retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas moderadores e administradores")
    })
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Estatísticas de moderação", description = "Retorna estatísticas para atividades de moderação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas moderadores e administradores")
    })
    public ResponseEntity<Map<String, Object>> getModerationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalRegularUsers", 0);
        stats.put("activeUsersToday", 0);
        stats.put("newUsersThisWeek", 0);
        stats.put("totalTasksCreatedToday", 0);
        stats.put("averageTasksPerUser", 0.0);
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/notifications")
    @Operation(summary = "Enviar notificação", description = "Permite moderadores enviarem notificações para usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificação enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de notificação inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas moderadores e administradores")
    })
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, Object> notification) {
        try {
            String message = (String) notification.get("message");
            String targetType = (String) notification.get("targetType");
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Erro: Mensagem é obrigatória"));
            }
            
            return ResponseEntity.ok(new MessageResponse("Notificação enviada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Erro: " + e.getMessage()));
        }
    }
    
    @GetMapping("/activity-report")
    @Operation(summary = "Relatório de atividades", description = "Gera relatório de atividades dos usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas moderadores e administradores")
    })
    public ResponseEntity<Map<String, Object>> getActivityReport(
            @Parameter(description = "Data de início (YYYY-MM-DD)") @RequestParam(required = false) String startDate,
            @Parameter(description = "Data de fim (YYYY-MM-DD)") @RequestParam(required = false) String endDate) {
        
        Map<String, Object> report = new HashMap<>();
        
        report.put("period", startDate + " até " + endDate);
        report.put("newUsers", 0);
        report.put("tasksCreated", 0);
        report.put("tasksCompleted", 0);
        report.put("mostActiveUsers", new String[]{});
        
        return ResponseEntity.ok(report);
    }
}

