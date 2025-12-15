package jomeerkatz.project.ai_flashcards.utility;

import jomeerkatz.project.ai_flashcards.domain.entities.User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;

public class JwtMapper {
    public static User toUser(Jwt jwt) {
        return User.builder()
                .keycloakId(jwt.getSubject())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
