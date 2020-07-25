package science.icebreaker.account;


/**
 * This exception will be thrown if a registration fails.
 */
public class AccountCreationException extends Exception {

    public AccountCreationException(String message) {
        super(message);
    }
}
