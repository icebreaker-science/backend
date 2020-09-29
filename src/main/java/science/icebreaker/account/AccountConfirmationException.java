package science.icebreaker.account;

public class AccountConfirmationException extends RuntimeException {

    private String message;

    public AccountConfirmationException(String message){
        super(message);
        this.message = message;
    }
}
