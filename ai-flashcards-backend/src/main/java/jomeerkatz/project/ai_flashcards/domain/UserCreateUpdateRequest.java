package jomeerkatz.project.ai_flashcards.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateUpdateRequest { // todo: create dto for it + check if the flow creating and find user is making sense
    private String keycloakId;
}
