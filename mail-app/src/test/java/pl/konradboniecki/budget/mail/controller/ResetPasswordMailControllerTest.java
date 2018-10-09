package pl.konradboniecki.budget.mail.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.mail.service.MailService;
import pl.konradboniecki.budget.mail.service.ResetPasswordService;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASResetPasswordDetails;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static pl.konradboniecki.budget.mail.controller.ResetPasswordMailController.BASE_PATH;

@ExtendWith(SpringExtension.class)
@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ResetPasswordMailControllerTest {
    private static final String RESET_PASSWD_PATH = BASE_PATH + "/password-reset";

    private String baseUrl;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private ResetPasswordService resetPasswordService;
    @MockBean
    private MailService mailService;

    private OASResetPasswordDetails resetPasswordDetails;
    private final HashMap<String, String> emptyUrlVariables = new HashMap<>();

    @BeforeAll
    public void setup() throws IOException {
        baseUrl = "http://localhost:" + port;
        String healthCheckUrl = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = rest.getForEntity(healthCheckUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"status\":\"UP\"}");

        OASAccount account = new OASAccount()
                .id(UUID.randomUUID())
                .firstName("john")
                .lastName("doe")
                .email("test@mail.com");
        resetPasswordDetails = new OASResetPasswordDetails()
                .account(account)
                .resetCode(UUID.randomUUID());
    }

    @Test
    public void givenResetPasswordRequest_whenSuccess_thenResponseIs200() {
        // Given:
        String url = baseUrl + RESET_PASSWD_PATH;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<OASResetPasswordDetails> entity = new HttpEntity<>(resetPasswordDetails, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void givenResetPasswordRequest_whenInvalidRequest_thenResponseIs400() {
        // Given:
        String url = baseUrl + RESET_PASSWD_PATH;
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
    public void givenBAHeaderIsMissing_whenInviteNewUser_thenUnauthorized() {
        // Given:
        String url = baseUrl + RESET_PASSWD_PATH;
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
