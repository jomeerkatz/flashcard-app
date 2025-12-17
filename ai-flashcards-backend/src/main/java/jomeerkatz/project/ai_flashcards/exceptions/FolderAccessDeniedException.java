package jomeerkatz.project.ai_flashcards.exceptions;

public class FolderAccessDeniedException extends BaseException{
    public FolderAccessDeniedException() {
        super();
    }

    public FolderAccessDeniedException(String message) {
        super(message);
    }

    public FolderAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FolderAccessDeniedException(Throwable cause) {
        super(cause);
    }
}
