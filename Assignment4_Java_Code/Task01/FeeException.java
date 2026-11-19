/** Business error that can be shown to the user as it is. */
public class FeeException extends Exception {
    public FeeException(String message) { super(message); }
    public FeeException(String message, Throwable cause) { super(message, cause); }
}
