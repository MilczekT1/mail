package pl.konradboniecki.budget.mail.cucumber.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.konradboniecki.budget.mail.cucumber.commons.SharedData;
import pl.konradboniecki.budget.mail.cucumber.security.Security;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASSignUpDetails;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class UserActivationSteps {

    private final Security security;
    private final TestRestTemplate testRestTemplate;
    private final SharedData sharedData;

    @Then("email with sign up confirmation is sent")
    public void emailWithInvitationIsSent() {
        HttpStatus lastResponseHttpStatus = sharedData.getLastResponseEntity().getStatusCode();
        assertThat(lastResponseHttpStatus.is2xxSuccessful()).isTrue();
    }

    @When("I send sign up confirmation email with activation link to (.+)$")
    public void iSendSignUpConfirmationEmailWithActivationLink(String email) {
        OASAccount acc = new OASAccount()
                .id(UUID.randomUUID())
                .email(email)
                .firstName("testFirstName")
                .lastName("testLastName");
        OASSignUpDetails signUpDetails = new OASSignUpDetails()
                .account(acc)
                .activationCode(UUID.randomUUID());
        HttpEntity<?> entity =
                new HttpEntity<>(signUpDetails, security.getSecurityHeaders());
        ResponseEntity<OASSignUpDetails> responseEntity = testRestTemplate
                .exchange("/api/mail/v1/account-activations", HttpMethod.POST, entity, OASSignUpDetails.class);
        sharedData.setLastResponseEntity(responseEntity);
    }
}
