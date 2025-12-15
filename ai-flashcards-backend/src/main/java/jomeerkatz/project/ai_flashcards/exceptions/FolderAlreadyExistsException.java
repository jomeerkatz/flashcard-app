package jomeerkatz.project.ai_flashcards.exceptions;

public class FolderAlreadyExistsException extends BaseException{
    public FolderAlreadyExistsException() {
        super();
    }

    public FolderAlreadyExistsException(String message) {
        super(message);
    }

    public FolderAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FolderAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
