package jomeerkatz.project.ai_flashcards.exceptions;

public class FolderDoesNotExists extends BaseException{
    public FolderDoesNotExists() {
        super();
    }

    public FolderDoesNotExists(String message) {
        super(message);
    }

    public FolderDoesNotExists(String message, Throwable cause) {
        super(message, cause);
    }

    public FolderDoesNotExists(Throwable cause) {
        super(cause);
    }
}
