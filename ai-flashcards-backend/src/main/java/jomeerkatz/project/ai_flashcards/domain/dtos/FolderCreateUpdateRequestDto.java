package jomeerkatz.project.ai_flashcards.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderCreateUpdateRequestDto {
    @NotBlank(message = "❌ folder name can't be blanked!")
    private String name;
}
