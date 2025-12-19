package jomeerkatz.project.ai_flashcards.domain.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jomeerkatz.project.ai_flashcards.domain.CardCreateUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkCardCreateRequestDto {
    @NotNull
    @Size(min = 1, max = 10, message = "Must provide between 1 and 10 cards")
    @Valid
    private List<CardCreateUpdateRequestDto> cards;
}
