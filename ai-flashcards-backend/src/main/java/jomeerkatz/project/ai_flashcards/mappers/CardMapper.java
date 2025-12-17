package jomeerkatz.project.ai_flashcards.mappers;

import jomeerkatz.project.ai_flashcards.domain.dtos.CardDto;
import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {
    CardDto toDto(Card card);
}
