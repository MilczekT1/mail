package pl.konradboniecki.budget.mail.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.mail.TestDataFactory;
import pl.konradboniecki.budget.mail.service.GuestUserInvitationService;
import pl.konradboniecki.budget.mail.service.MailService;
import pl.konradboniecki.budget.mail.service.RegisteredUserInvitationService;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class InvitationToFamilyMailControllerTest {
    private static final String INVITATION_PATH = "/api/mail/v1/family-invitations";

    private String baseUrl;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private RegisteredUserInvitationService registeredUserInvitationService;
    @Autowired
    private GuestUserInvitationService guestUserInvitationService;
    @MockBean
    private MailService mailService;

    private OASInvitationToFamily invitationToFamilyForNewUser;
    private OASInvitationToFamily invitationToFamilyForExistingUser;
    private String invalidRequest;
    private HashMap<String, String> emptyUrlVariables = new HashMap<>();

    @BeforeAll
    void setup() throws IOException {
        baseUrl = "http://localhost:" + port;
        String healthCheckUrl = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = rest.getForEntity(healthCheckUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"status\":\"UP\"}");

        OASAccount inviter = TestDataFactory.populateValidAccount();
        OASAccount account = TestDataFactory.populateValidAccount();
        OASFamily family = TestDataFactory.populateValidFamily();

        invitationToFamilyForNewUser = new OASInvitationToFamily()
                .guest(true)
                .inviter(inviter)
                .family(family)
                .email("test@mail.com");
        invitationToFamilyForExistingUser = new OASInvitationToFamily()
                .guest(false)
                .inviter(inviter)
                .family(family)
                .invitee(account)
                .email("john@doe.com")
                .invitationCode(UUID.randomUUID());
        invalidRequest = "{}";
    }

    @Test
    void givenNewUserInvitationRequest_whenSuccess_thenResponseIs200() {
        // Given:
        String url = baseUrl + INVITATION_PATH;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<OASInvitationToFamily> entity = new HttpEntity<>(invitationToFamilyForNewUser, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void givenNewUserInvitationRequest_whenInvalidRequest_thenResponseIs400() {
        // Given:
        String url = baseUrl + INVITATION_PATH;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<Void> entity = new HttpEntity<>(null, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenOldUserInvitationRequest_whenSuccess_thenResponseIs200() {
        // Given:
        String url = baseUrl + INVITATION_PATH;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<OASInvitationToFamily> entity = new HttpEntity<>(invitationToFamilyForExistingUser, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void givenOldUserInvitationRequest_whenInvalidRequest_thenResponseIs400() {
        // Given:
        String url = baseUrl + INVITATION_PATH;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<Void> entity = new HttpEntity<>(null, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenBAHeaderIsMissing_whenInvitingToFamily_thenUnauthorized() {
        // Given:
        String url = baseUrl + INVITATION_PATH;
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
