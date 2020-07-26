package science.icebreaker.account;

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
        return request;
    }

}
