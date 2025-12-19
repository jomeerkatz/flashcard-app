package jomeerkatz.project.ai_flashcards.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkCardCreateRequest {
    private List<CardCreateUpdateRequest> cards;
}

