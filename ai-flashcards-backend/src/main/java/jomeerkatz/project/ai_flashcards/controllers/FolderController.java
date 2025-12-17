package jomeerkatz.project.ai_flashcards.controllers;

import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderDto;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.mappers.FolderMapper;
import jomeerkatz.project.ai_flashcards.services.FolderService;
import jomeerkatz.project.ai_flashcards.utility.JwtMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(path = "/api/folders")
@RequiredArgsConstructor
@Slf4j
public class FolderController {
    private final FolderService folderService;
    private final FolderMapper folderMapper;

    @PostMapping
    public ResponseEntity<FolderDto> createFolder(@AuthenticationPrincipal Jwt jwt, FolderCreateUpdateRequest folderCreateUpdateRequest) {
        User newUser = JwtMapper.toUser(jwt);
        Folder savedFolder = folderService.createFolder(newUser, folderCreateUpdateRequest);
        return ResponseEntity.ok(folderMapper.toFolderDto(savedFolder));
    }

    @GetMapping
    public Page<FolderDto> getAllFolder(@AuthenticationPrincipal Jwt jwt, @PageableDefault(size = 5, page = 0) Pageable pageable) {
        User user = JwtMapper.toUser(jwt);

        log.info("❌❌❌❌ User object: {}", user);
        log.debug("❌❌❌❌ User keycloakId: {}", user.getKeycloakId());


        return folderService.getAllFolders(user, pageable)
                .map(folderMapper::toFolderDto);
    }
}
