package science.icebreaker.account;


public class RegistrationRequest {

    private Account account;

    private Profile profile;


    public Account getAccount() {
        return account;
    }


    public void setAccount(Account account) {
        this.account = account;
    }


    public Profile getProfile() {
        return profile;
    }


    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
