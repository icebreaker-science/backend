package science.icebreaker.integration;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountConfirmation;
import science.icebreaker.dao.repository.AccountConfirmationRepository;
import science.icebreaker.dao.repository.AccountRepository;
import science.icebreaker.data.request.RegistrationRequest;
import science.icebreaker.util.mixins.JacksonMixins.IgnoreIdMixIn;
import science.icebreaker.util.mock.RegistrationRequestMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_METHOD)
public class UserAccountCreation {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountConfirmationRepository accountConfirmationRepository;
    @Autowired
    private AccountRepository accountRepository;
    
    private Account account;
    private String accountToken;


    @Test
    public void account_correctData_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(Account.class, IgnoreIdMixIn.class);
        mapper.setSerializationInclusion(Include.NON_NULL);

        RegistrationRequest registerReq = RegistrationRequestMock.createRegistrationRequest();
        this.account = registerReq.getAccount();

        // Create an account
        String registerReqString = mapper.writeValueAsString(registerReq);
        MvcResult registerRes = mockMvc.perform(
                post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerReqString)
        ).andExpect(status().isOk()).andReturn();

        int returnedAccountId = Integer.parseInt(registerRes.getResponse().getContentAsString());
        this.account.setId(returnedAccountId);

        // TODO: obtain from a mocked email service
        Optional<AccountConfirmation> tokenLookupRes = accountConfirmationRepository
            .findAccountConfirmationByAccount(this.account);

        assertThat(tokenLookupRes).isNotEmpty();
        String loginDataString = mapper.writeValueAsString(this.account);

        // Try login, should fail as account no yet validated
        mockMvc.perform(
                post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDataString)
        ).andExpect(status().isUnauthorized());

        // validate email
        String emailToken = tokenLookupRes.get().getConfirmationToken();
        mockMvc.perform(
                post("/account/validate-email")
                .param("key", emailToken)
        ).andExpect(status().isOk());

        // Try login, should return ok now
        MvcResult loginRes = mockMvc.perform(
                post("/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDataString)
        ).andExpect(status().isOk()).andReturn();

        String token = loginRes.getResponse().getContentAsString();
        this.accountToken = token;

        // Issuing a request which requires authorization passes 
        mockMvc.perform(
                get("/account/my-profile")
                .header("authorization", "Bearer " + token)
        ).andExpect(status().isOk());

        // Logout and invalidate the token
        mockMvc.perform(
                post("/account/logout")
                .header("authorization", "Bearer " + token)
        ).andExpect(status().isOk()).andReturn();

        // Issuing a request which requires authorization should fail now
        assertThatThrownBy(() ->
            mockMvc.perform(
                get("/account/my-profile")
                .header("authorization", "Bearer " + token)
            )
        ).isInstanceOf(JwtException.class);
    }

    @AfterEach
    public void cleanup() {
        // Transactional is not possible since logins do a manual query
        this.accountConfirmationRepository.deleteAccountConfirmationByConfirmationToken(this.accountToken);
        this.accountRepository.delete(this.account);
    }
}
