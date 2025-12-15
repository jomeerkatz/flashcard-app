package jomeerkatz.project.ai_flashcards.mappers;

import jomeerkatz.project.ai_flashcards.domain.dtos.UserDto;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toUserDto(User user);
}
