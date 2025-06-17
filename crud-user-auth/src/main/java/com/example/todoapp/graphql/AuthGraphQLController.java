package com.example.todoapp.graphql;

import com.example.todoapp.domain.enums.ERole;
import com.example.todoapp.domain.Role;
import com.example.todoapp.domain.User;
import com.example.todoapp.payload.response.JwtResponse;
import com.example.todoapp.payload.response.MessageResponse;
import com.example.todoapp.repository.RoleRepository;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.security.jwt.JwtUtils;
import com.example.todoapp.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AuthGraphQLController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @MutationMapping
    public JwtResponse login(@Argument String username, @Argument String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt, 
                               userDetails.getId(), 
                               userDetails.getUsername(), 
                               userDetails.getEmail(), 
                               roles);
    }

    @MutationMapping
    public MessageResponse register(@Argument String username, @Argument String email, 
                                   @Argument String password, @Argument List<String> roles) {
        if (userRepository.existsByUsername(username)) {
            return new MessageResponse("Erro: Nome de usuário já está em uso!");
        }

        if (userRepository.existsByEmail(email)) {
            return new MessageResponse("Erro: Email já está em uso!");
        }

        User user = new User(username, email, encoder.encode(password));

        Set<Role> userRoles = new HashSet<>();

        if (roles == null || roles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
            userRoles.add(userRole);
        } else {
            roles.forEach(role -> {
                switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                    userRoles.add(adminRole);
                    break;
                case "mod":
                    Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                            .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                    userRoles.add(modRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                    userRoles.add(userRole);
                }
            });
        }

        user.setRoles(userRoles);
        userRepository.save(user);

        return new MessageResponse("Usuário registrado com sucesso!");
    }
}
