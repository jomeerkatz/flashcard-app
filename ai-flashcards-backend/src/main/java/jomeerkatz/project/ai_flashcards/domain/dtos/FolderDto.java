package jomeerkatz.project.ai_flashcards.domain.dtos;

import jakarta.persistence.*;
import jomeerkatz.project.ai_flashcards.domain.entities.User;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderDto {
    private Long id;

    private String name;
}
