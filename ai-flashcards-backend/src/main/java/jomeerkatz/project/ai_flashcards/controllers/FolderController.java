package jomeerkatz.project.ai_flashcards.controllers;

import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderDto;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.mappers.FolderMapper;
import jomeerkatz.project.ai_flashcards.services.FolderService;
import jomeerkatz.project.ai_flashcards.utility.JwtMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/api/folders")
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;
    private final FolderMapper folderMapper;

    @PostMapping
    public ResponseEntity<FolderDto> createFolder(@AuthenticationPrincipal Jwt jwt, FolderCreateUpdateRequest folderCreateUpdateRequest) {
        User newUser = JwtMapper.toUser(jwt);
        Folder savedFolder = folderService.createFolder(newUser, folderCreateUpdateRequest);
        return ResponseEntity.ok(folderMapper.toFolderDto(savedFolder));
    }
}
