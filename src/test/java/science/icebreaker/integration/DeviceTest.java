package science.icebreaker.integration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import science.icebreaker.service.AccountService;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.dao.entity.WikiPage.PageType;
import science.icebreaker.dao.repository.AccountRepository;
import science.icebreaker.data.request.AddDeviceAvailabilityRequest;
import science.icebreaker.data.request.ContactRequest;
import science.icebreaker.data.request.RegistrationRequest;
import science.icebreaker.util.TestHelper;
import science.icebreaker.util.mock.RegistrationRequestMock;
import com.jayway.jsonpath.JsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_METHOD)
public class DeviceTest {
    
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TestHelper testHelper;
    @Autowired
    private AccountService accountService;
    
    private Account deviceOwner;
    private String deviceOwnerToken;
    private Account deviceRequester;
    private String deviceRequesterToken;

    @BeforeEach
    public void setupData() {
        RegistrationRequest ownerAccountData = RegistrationRequestMock.createRegistrationRequest();
        this.deviceOwner = testHelper.createAccount();

        Account ownerAccountLoginData = new Account();
        ownerAccountLoginData.setEmail(ownerAccountData.getAccount().getEmail());
        ownerAccountLoginData.setPassword(ownerAccountData.getAccount().getPassword());
        
        this.deviceOwnerToken = this.accountService.login(ownerAccountLoginData);


        RegistrationRequest requesterAccountData = RegistrationRequestMock.createRegistrationRequest2();
        this.deviceRequester = testHelper.createAccount2();
        
        Account requesterAccountLoginData = new Account();
        requesterAccountLoginData.setEmail(requesterAccountData.getAccount().getEmail());
        requesterAccountLoginData.setPassword(requesterAccountData.getAccount().getPassword());
        this.deviceRequesterToken = this.accountService.login(requesterAccountLoginData);
    }

    @Test
    @Transactional
    public void device_correctData_succss() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);

        WikiPage device = new WikiPage(PageType.DEVICE, "DEVICE 1", "DESCRIPTION", "REFS");

        // Add device by owner
        MvcResult deviceAddRes = mockMvc.perform(
            MockMvcRequestBuilders.multipart("/wiki")
            .param("title", device.getTitle())
            .param("type", device.getType().name())
            .param("references", device.getReferences())
            .param("description", device.getDescription())
            .param("networkKeywords", String.join(",", device.getNetworkKeywords()))
            .header("authorization", "Bearer " + this.deviceOwnerToken)
        ).andExpect(status().isOk()).andReturn();

        Integer addedDeviceId = Integer.parseInt(deviceAddRes.getResponse().getContentAsString());

        // Requester finds the device
        mockMvc.perform(
            get("/wiki")
            .param("type", PageType.DEVICE.name())
            .header("authorization", "Bearer " + this.deviceRequesterToken)
        ).andExpect(status().isOk())

        // Json Path: one element has an ID matching `addedDeviceId`
        .andExpect(jsonPath(String.format("$.[?(@.id == %s)]", addedDeviceId)).isNotEmpty());


        AddDeviceAvailabilityRequest deviceRequest = new AddDeviceAvailabilityRequest(
                addedDeviceId, "Comment on device", 
                "99999", "Insitution", 
                "Research Group"
        );

        String deviceRequestString = mapper.writeValueAsString(deviceRequest);
        // Owner adds an availability listing
        mockMvc.perform(
            post("/device-availability/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(deviceRequestString)
            .header("authorization", "Bearer " + this.deviceOwnerToken)
        ).andExpect(status().isOk());

        // Requester finds the availability listing
        MvcResult lookupAvailabilityRes = mockMvc.perform(
            get("/device-availability/")
            .param("device", addedDeviceId.toString())
        ).andExpect(status().isOk()).andReturn();
        // Json Path: first id in list
        Integer availabilityId = JsonPath
            .read(lookupAvailabilityRes.getResponse().getContentAsString(), "$.[0].id");


        // TODO: test the mail
        ContactRequest contactRequest = new ContactRequest(
            this.deviceRequester.getName(), this.deviceRequester.getEmail(),
            "Contact Message", "10000000-aaaa-bbbb-cccc-000000000001"
        );

        String contactRequestString = mapper.writeValueAsString(contactRequest);
        // Requester requests the device
        mockMvc.perform(
            post("/device-availability/" + availabilityId + "/contact")
            .contentType(MediaType.APPLICATION_JSON)
            .content(contactRequestString)
            .header("authorization", "Bearer " + this.deviceRequesterToken)
        ).andExpect(status().isOk());

    }

    @AfterEach
    public void cleanUp() {
        List<Account> accountList = Arrays.asList(deviceOwner, deviceRequester);
        this.accountRepository.deleteAll(accountList);
    }
}
