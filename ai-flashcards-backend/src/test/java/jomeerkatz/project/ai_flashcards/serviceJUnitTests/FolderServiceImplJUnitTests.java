package jomeerkatz.project.ai_flashcards.serviceJUnitTests;

import jomeerkatz.project.ai_flashcards.DataUtil;
import jomeerkatz.project.ai_flashcards.domain.entities.Folder;
import jomeerkatz.project.ai_flashcards.domain.entities.User;
import jomeerkatz.project.ai_flashcards.exceptions.FolderAlreadyExistsException;
import jomeerkatz.project.ai_flashcards.repositories.FolderRepository;
import jomeerkatz.project.ai_flashcards.services.UserService;
import jomeerkatz.project.ai_flashcards.services.impl.FolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FolderServiceImplJUnitTests {
    @Mock
    private FolderRepository folderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FolderServiceImpl folderService;

    @Test
    public void TestThatCorrectlySavedFolderAndReturnsSavedFolderSuccessfully() {
        User newUser = DataUtil.getUserExample1WithId();
        Folder newFolder = DataUtil.getFolderExample1WithId(newUser);

        when(userService.getUserOrThrow(newUser))
                .thenReturn(newUser);
        when(folderRepository.existsByUserIdAndName(any(), any()))
                .thenReturn(false);
        when(folderRepository.save(any()))
                .thenReturn(newFolder);

        Folder result = folderService.saveFolder(newUser, DataUtil.getFolderCreateUpdateRequest());

        assertThat(result).isEqualTo(newFolder);

    }

    @Test
    public void TestThatCorrectlyDoNotSaveFolderAndReturnsFolderAlreadyExistsException() {
        User newUser = DataUtil.getUserExample1WithId();
        Folder newFolder = DataUtil.getFolderExample1WithId(newUser);

        when(userService.getUserOrThrow(newUser))
                .thenReturn(newUser);
        when(folderRepository.existsByUserIdAndName(any(), any()))
                .thenReturn(true);


        assertThatThrownBy(() -> folderService.saveFolder(newUser, DataUtil.getFolderCreateUpdateRequest()))
                .isInstanceOf(FolderAlreadyExistsException.class)
                        .hasMessage("folder with name " + DataUtil.getFolderCreateUpdateRequest().getName() + " already exists!");

    }
    // more tests could be implemented later. todo:
    //    Folder saveFolder(User user, FolderCreateUpdateRequest folderCreateUpdateRequest);
    //    Page<Folder> getAllFolders(User user, Pageable pageable);
    //    void updateFolder(User user, Long folderId, FolderCreateUpdateRequest folderCreateUpdateRequest);
    //    void deleteFolder(User user, Long folderId)

}
