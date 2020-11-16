package science.icebreaker.data.request;


import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;

public class RegistrationRequest {

    private Account account;

    private AccountProfile profile;


    public Account getAccount() {
        return account;
    }


    public void setAccount(Account account) {
        this.account = account;
    }


    public AccountProfile getProfile() {
        return profile;
    }


    public void setProfile(AccountProfile profile) {
        this.profile = profile;
    }
}
