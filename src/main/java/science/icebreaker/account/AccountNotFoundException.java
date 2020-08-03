package science.icebreaker.account;


/**
 * This exception will be thrown if an account does not exist.
 */
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(int userId) {
        super("There is no account with the ID " + userId + ".");
    }
}
