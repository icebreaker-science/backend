package science.icebreaker.util.mock;

import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.data.request.RegistrationRequest;

public class RegistrationRequestMock {

    public static RegistrationRequest createRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        Account account = new Account(null, "a.friend@icebreaker.science", "frosty");
        AccountProfile accountProfile = new AccountProfile();
        accountProfile.setForename("A");
        accountProfile.setSurname("Friend");
        accountProfile.setInstitution("TUM");
        accountProfile.setCity("Munich");
        accountProfile.setResearchArea("Water");

        request.setAccount(account);
        request.setProfile(accountProfile);
        request.setCaptcha("captcha-token");
        return request;
    }

    public static RegistrationRequest createRegistrationRequest2() {
        RegistrationRequest request = new RegistrationRequest();
        Account account = new Account(null, "b.friend@icebreaker.science", "cold");
        AccountProfile accountProfile = new AccountProfile();
        accountProfile.setForename("B");
        accountProfile.setSurname("Friend");
        accountProfile.setInstitution("TU KL");
        accountProfile.setCity("Kaiserslautern");
        accountProfile.setResearchArea("Air");

        request.setAccount(account);
        request.setProfile(accountProfile);
        request.setCaptcha("captcha-token");
        return request;
    }

}
