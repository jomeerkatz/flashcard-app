package jomeerkatz.project.ai_flashcards.controllers;

import jakarta.validation.Valid;
import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.CardDto;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderCreateUpdateRequestDto;
import jomeerkatz.project.ai_flashcards.domain.dtos.FolderDto;
import jomeerkatz.project.ai_flashcards.domain.entities.Card;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.mappers.CardMapper;
import jomeerkatz.project.ai_flashcards.mappers.FolderMapper;
import jomeerkatz.project.ai_flashcards.services.CardService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/folders")
@RequiredArgsConstructor
@Slf4j
public class FolderController {
    private final FolderService folderService;
    private final FolderMapper folderMapper;
    private final CardService cardService;
    private final CardMapper cardMapper;

    @PostMapping
    public ResponseEntity<FolderDto> createFolder(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody FolderCreateUpdateRequestDto folderCreateUpdateRequestDto) {
        User newUser = JwtMapper.toUser(jwt);
        FolderCreateUpdateRequest folderCreateUpdateRequest = folderMapper.toFolderCreateUpdateRequest(folderCreateUpdateRequestDto);
        Folder savedFolder = folderService.saveFolder(newUser, folderCreateUpdateRequest);
        return ResponseEntity.ok(folderMapper.toFolderDto(savedFolder));
    }

    @GetMapping
    public Page<FolderDto> getAllFolder(@AuthenticationPrincipal Jwt jwt, @PageableDefault(size = 5, page = 0) Pageable pageable) {
        User user = JwtMapper.toUser(jwt);
        return folderService.getAllFolders(user, pageable)
                .map(folderMapper::toFolderDto);
    }

    @GetMapping(path = "/{folderId}")
    public Page<CardDto> getAllCardsOfFolder(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable(name = "folderId") Long folderId,
                                             @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return cardService.getAllCards(JwtMapper.toUser(jwt), folderId, pageable).map(cardMapper::toDto);
    }
}
