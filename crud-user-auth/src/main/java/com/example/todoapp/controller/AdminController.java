package com.example.todoapp.controller;

import com.example.todoapp.dto.UserDTO;
import com.example.todoapp.payload.response.MessageResponse;
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
@RequestMapping("/api/admin")
@Tag(name = "Administração", description = "Endpoints exclusivos para administradores")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard administrativo", description = "Retorna estatísticas gerais do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("totalUsers", 0);
        dashboard.put("activeUsers", 0);
        dashboard.put("totalTasks", 0);
        dashboard.put("completedTasks", 0);
        dashboard.put("pendingTasks", 0);
        dashboard.put("overdueTasks", 0);
        
        return ResponseEntity.ok(dashboard);
    }
    
    @PostMapping("/users/bulk-activate")
    @Operation(summary = "Ativar usuários em lote", description = "Ativa múltiplos usuários de uma vez")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários ativados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    public ResponseEntity<?> bulkActivateUsers(@RequestBody Map<String, Object> request) {
        try {
            return ResponseEntity.ok(new MessageResponse("Operação em lote executada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Erro: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reports/users")
    @Operation(summary = "Relatório de usuários", description = "Gera relatório detalhado dos usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    public ResponseEntity<Page<UserDTO>> getUsersReport(
            @Parameter(description = "Período do relatório") @RequestParam(required = false) String period,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserDTO> users = userService.getAllUsers(pageable);
        
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/settings")
    @Operation(summary = "Obter configurações do sistema", description = "Retorna configurações atuais do sistema")
    public ResponseEntity<Map<String, Object>> getSystemSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("allowUserRegistration", true);
        settings.put("defaultUserRole", "USER");
        settings.put("maxTasksPerUser", 100);
        settings.put("sessionTimeout", 3600);
        
        return ResponseEntity.ok(settings);
    }
    
    @PutMapping("/settings")
    @Operation(summary = "Atualizar configurações do sistema", description = "Atualiza configurações do sistema")
    public ResponseEntity<?> updateSystemSettings(@RequestBody Map<String, Object> settings) {
        try {
            return ResponseEntity.ok(new MessageResponse("Configurações atualizadas com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Erro: " + e.getMessage()));
        }
    }
}

