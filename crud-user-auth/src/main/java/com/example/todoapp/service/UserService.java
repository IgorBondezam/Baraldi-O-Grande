package com.example.todoapp.service;

import com.example.todoapp.domain.enums.ERole;
import com.example.todoapp.domain.Role;
import com.example.todoapp.domain.User;
import com.example.todoapp.dto.UserDTO;
import com.example.todoapp.repository.RoleRepository;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder encoder;
    
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        validateAdminAccess();
        return userRepository.findAll(pageable)
                .map(UserDTO::fromEntity);
    }
    
    public Optional<UserDTO> getUserById(Long id) {
        User currentUser = getCurrentUser();
        
        if (!hasRole("ADMIN") && !currentUser.getId().equals(id)) {
            throw new RuntimeException("Acesso negado: você só pode acessar seu próprio perfil");
        }
        
        return userRepository.findById(id)
                .map(UserDTO::fromEntity);
    }
    
    public Optional<UserDTO> getUserByUsername(String username) {
        User currentUser = getCurrentUser();
        
        if (!hasRole("ADMIN") && !currentUser.getUsername().equals(username)) {
            throw new RuntimeException("Acesso negado: você só pode acessar seu próprio perfil");
        }
        
        return userRepository.findByUsername(username)
                .map(UserDTO::fromEntity);
    }
    
    public UserDTO createUser(UserDTO userDTO) {
        validateAdminAccess();
        
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Erro: Username já está em uso!");
        }
        
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Erro: Email já está em uso!");
        }
        
        User user = userDTO.toEntity();
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        Set<Role> roles = new HashSet<>();
        if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
            roles.add(userRole);
        } else {
            userDTO.getRoles().forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                        roles.add(userRole);
                }
            });
        }
        
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        return UserDTO.fromEntity(savedUser);
    }
    
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User currentUser = getCurrentUser();
        
        if (!hasRole("ADMIN") && !currentUser.getId().equals(id)) {
            throw new RuntimeException("Acesso negado: você só pode atualizar seu próprio perfil");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        if (!user.getUsername().equals(userDTO.getUsername()) &&
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Erro: Username já está em uso!");
        }
        
        if (!user.getEmail().equals(userDTO.getEmail()) &&
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Erro: Email já está em uso!");
        }
        
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setUpdatedAt(LocalDateTime.now());
        
        if (hasRole("ADMIN")) {
            user.setActive(userDTO.isActive());
            
            if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
                Set<Role> roles = new HashSet<>();
                userDTO.getRoles().forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                            roles.add(adminRole);
                            break;
                        case "mod":
                            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                            roles.add(modRole);
                            break;
                        default:
                            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                            roles.add(userRole);
                    }
                });
                user.setRoles(roles);
            }
        }
        
        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }
    
    public void changePassword(Long id, String currentPassword, String newPassword) {
        User currentUser = getCurrentUser();
        
        if (!hasRole("ADMIN") && !currentUser.getId().equals(id)) {
            throw new RuntimeException("Acesso negado: você só pode alterar sua própria senha");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        if (!hasRole("ADMIN") || currentUser.getId().equals(id)) {
            if (!encoder.matches(currentPassword, user.getPassword())) {
                throw new RuntimeException("Senha atual incorreta");
            }
        }
        
        user.setPassword(encoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void deactivateUser(Long id) {
        validateAdminAccess();
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void activateUser(Long id) {
        validateAdminAccess();
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        validateAdminAccess();
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        userRepository.delete(user);
    }
    
    public Page<UserDTO> getUsersByRole(String roleName, Pageable pageable) {
        if (!hasRole("ADMIN") && !hasRole("MODERATOR")) {
            throw new RuntimeException("Acesso negado: apenas administradores e moderadores podem listar usuários por role");
        }
        
        ERole role;
        try {
            role = ERole.valueOf("ROLE_" + roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Role inválida: " + roleName);
        }
        
        return userRepository.findByRoles_Name(role, pageable)
                .map(UserDTO::fromEntity);
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuário atual não encontrado"));
    }
    
    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
    }
    
    private void validateAdminAccess() {
        if (!hasRole("ADMIN")) {
            throw new RuntimeException("Acesso negado: apenas administradores podem realizar esta operação");
        }
    }
}

