package jomeerkatz.project.ai_flashcards.controllers;

import jakarta.validation.Valid;
import jomeerkatz.project.ai_flashcards.domain.CardCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.dtos.CardCreateUpdateRequestDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
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

    @PutMapping(path = "/{folderId}")
    public ResponseEntity<Void> updateFolder(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable(name="folderId") Long folderId,
                                             @Valid @RequestBody FolderCreateUpdateRequestDto folderCreateUpdateRequestDto) {
        User user = JwtMapper.toUser(jwt);
        folderService.updateFolder(user, folderId, folderMapper.toFolderCreateUpdateRequest(folderCreateUpdateRequestDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/{folderId}")
    public ResponseEntity<Void> deleteFolder(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable(name = "folderId") Long folderId
                                             ) {
        User user = JwtMapper.toUser(jwt);
        folderService.deleteFolder(user, folderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/{folderId}/cards/count")
    public ResponseEntity<Long> getCountOfCardsOfSpecificFolder(@AuthenticationPrincipal Jwt jwt,
                                                                @PathVariable(name = "folderId") Long folderId) {
        User user = JwtMapper.toUser(jwt);
        return ResponseEntity.ok(cardService.getCountOfCardsByFoldeId(user, folderId));
    }

    @PostMapping(path = "/{folderId}/cards")
    public ResponseEntity<CardDto> createCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable(name = "folderId") Long folderId,
            @Valid @RequestBody CardCreateUpdateRequestDto cardCreateUpdateRequestDto
            ) {
        User user = JwtMapper.toUser(jwt);
        CardCreateUpdateRequest cardCreateUpdateRequest = cardMapper.toCardCreateUpdateRequest(cardCreateUpdateRequestDto);
        return ResponseEntity.ok(cardMapper.toDto(cardService.createCard(user, folderId, cardCreateUpdateRequest)));
    }

    @PutMapping(path = "/{folderId}/cards/{cardId}")
    public ResponseEntity<Void> updateCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable(name = "folderId") Long folderId,
            @PathVariable(name = "cardId") Long cardId,
            @Valid @RequestBody CardCreateUpdateRequestDto cardCreateUpdateRequestDto
    ) {
        User user = JwtMapper.toUser(jwt);
        CardCreateUpdateRequest cardCreateUpdateRequest = cardMapper.toCardCreateUpdateRequest(cardCreateUpdateRequestDto);
        cardService.updateCard(user, folderId, cardCreateUpdateRequest, cardId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/{folderId}/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@AuthenticationPrincipal Jwt jwt,
                      @PathVariable(name = "folderId") Long folderId,
                      @PathVariable(name = "cardId") Long cardId) {
        User user = JwtMapper.toUser(jwt);
        cardService.deleteCard(user, folderId, cardId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
