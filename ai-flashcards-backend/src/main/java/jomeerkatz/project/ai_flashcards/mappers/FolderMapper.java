package jomeerkatz.project.ai_flashcards.mappers;

import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderCreateUpdateRequestDto;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderDto;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FolderMapper {
    FolderDto toFolderDto(Folder folder);
    FolderCreateUpdateRequest toFolderCreateUpdateRequest(FolderCreateUpdateRequestDto folderCreateUpdateRequestDto);
}
