package pl.konradboniecki.budget.mail.cucumber.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import pl.konradboniecki.budget.mail.cucumber.commons.SharedData;
import pl.konradboniecki.budget.mail.cucumber.security.Security;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASResetPasswordDetails;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class ResetPasswordSteps {

    private final Security security;
    private final TestRestTemplate testRestTemplate;
    private final SharedData sharedData;

    @Then("email with reset password link is sent")
    public void emailWithInvitationIsSent() {
        HttpStatusCode lastResponseHttpStatus = sharedData.getLastResponseEntity().getStatusCode();
        assertThat(lastResponseHttpStatus.is2xxSuccessful()).isTrue();
    }

    @When("I send email with reset password link to (.+)$")
    public void iSendEmailWithResetPasswordLink(String email) {
        OASAccount account = new OASAccount()
                .id(UUID.randomUUID())
                .email(email)
                .firstName("testFirstName")
                .lastName("testLastName");

        OASResetPasswordDetails resetPasswordDetails = new OASResetPasswordDetails()
                .account(account)
                .resetCode(UUID.randomUUID());
        HttpEntity<?> entity =
                new HttpEntity<>(resetPasswordDetails, security.getSecurityHeaders());
        ResponseEntity<OASResetPasswordDetails> responseEntity = testRestTemplate
                .exchange("/api/mail/v1/password-reset", HttpMethod.POST, entity, OASResetPasswordDetails.class);
        sharedData.setLastResponseEntity(responseEntity);
    }
}
