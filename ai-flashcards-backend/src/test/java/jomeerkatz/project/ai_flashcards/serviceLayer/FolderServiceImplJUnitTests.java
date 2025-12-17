package jomeerkatz.project.ai_flashcards.serviceLayer;

import jomeerkatz.project.ai_flashcards.DataUtil;
import jomeerkatz.project.ai_flashcards.domain.FolderCreateUpdateRequest;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.exceptions.FolderAlreadyExistsException;
import jomeerkatz.project.ai_flashcards.exceptions.UserNotFoundException;
import jomeerkatz.project.ai_flashcards.mappers.FolderMapper;
import jomeerkatz.project.ai_flashcards.repositories.FolderRepository;
import jomeerkatz.project.ai_flashcards.repositories.UserRepository;
import jomeerkatz.project.ai_flashcards.services.impl.FolderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FolderServiceImplJUnitTests {

    @InjectMocks
    private FolderServiceImpl folderService;

    @MockitoBean
    private FolderRepository folderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FolderMapper folderMapper;

    private User testUser;
    private FolderCreateUpdateRequest folderRequest;

    @BeforeEach
    void setUp() {
        testUser = DataUtil.getUserExample(DataUtil.getKeycloakExampleId1());
        testUser.setId(1L);
        folderRequest = new FolderCreateUpdateRequest(DataUtil.getFolderTestName1());
    }

    // ==================== createFolder() Tests ====================

    @Test
    public void testThatCreatesFolderSuccessfullyWhenFolderDoesNotExist() {
        // Arrange
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));

        when(folderRepository.existsByUserIdAndName(testUser.getId(), folderRequest.getName()))
                .thenReturn(false);

        Folder expectedFolder = Folder.builder()
                .id(1L)
                .name(folderRequest.getName())
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(folderRepository.save(any(Folder.class)))
                .thenReturn(expectedFolder);

        // Act
        Folder result = folderService.saveFolder(testUser, folderRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(folderRequest.getName());
        assertThat(result.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    public void testThatThrowsFolderAlreadyExistsExceptionWhenFolderExists() {
        // Arrange
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));

        when(folderRepository.existsByUserIdAndName(testUser.getId(), folderRequest.getName()))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> folderService.saveFolder(testUser, folderRequest))
                .isInstanceOf(FolderAlreadyExistsException.class)
                .hasMessageContaining(folderRequest.getName())
                .hasMessageContaining("already exists");
    }

    @Test
    public void testThatThrowsUserNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> folderService.saveFolder(testUser, folderRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("doesn't exists");
    }

    // ==================== getAllFolders() Tests ====================

    @Test
    public void testThatReturnsPagedFoldersWhenUserExists() {
        // Arrange
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));

        Folder folder1 = DataUtil.getFolderExample(testUser, DataUtil.getFolderTestName1());
        Folder folder2 = DataUtil.getFolderExample(testUser, DataUtil.getFolderTestName2());
        Folder folder3 = DataUtil.getFolderExample(testUser, DataUtil.getFolderTestName3());

        Page<Folder> expectedPage = new PageImpl<>(List.of(folder1, folder2, folder3));

        when(folderRepository.findAllByUserId(testUser.getId(), PageRequest.of(0, 10)))
                .thenReturn(expectedPage);

        // Act
        Page<Folder> result = folderService.getAllFolders(testUser, PageRequest.of(0, 10));

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    public void testThatReturnsEmptyPageWhenUserHasNoFolders() {
        // Arrange
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));

        Page<Folder> emptyPage = new PageImpl<>(List.of());

        when(folderRepository.findAllByUserId(testUser.getId(), PageRequest.of(0, 10)))
                .thenReturn(emptyPage);

        // Act
        Page<Folder> result = folderService.getAllFolders(testUser, PageRequest.of(0, 10));

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void testThatThrowsUserNotFoundExceptionWhenUserDoesNotExistInGetAllFolders() {
        // Arrange
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> folderService.getAllFolders(testUser, PageRequest.of(0, 10)))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("doesn't exists");
    }

    @Test
    public void testThatRespectsPaginationParametersInGetAllFolders() {
        // Arrange
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));

        Folder folder1 = DataUtil.getFolderExample(testUser, DataUtil.getFolderTestName1());
        Page<Folder> firstPage = new PageImpl<>(List.of(folder1), PageRequest.of(0, 1), 3);

        when(folderRepository.findAllByUserId(testUser.getId(), PageRequest.of(0, 1)))
                .thenReturn(firstPage);

        // Act
        Page<Folder> result = folderService.getAllFolders(testUser, PageRequest.of(0, 1));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }
}