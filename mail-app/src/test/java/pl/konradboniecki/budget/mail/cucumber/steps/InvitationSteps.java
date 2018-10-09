package pl.konradboniecki.budget.mail.cucumber.steps;

import io.cucumber.java.en.And;
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
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class InvitationSteps {

    private final Security security;
    private final TestRestTemplate testRestTemplate;
    private final SharedData sharedData;

    @Then("the operation is a success")
    public void theOperationIsASuccess() {
        HttpStatus lastResponseHttpStatus = sharedData.getLastResponseEntity().getStatusCode();
        assertThat(lastResponseHttpStatus.is2xxSuccessful()).isTrue();
    }

    @And("I have a family")
    public void iHaveAFamily() {
        sharedData.getOrCreateFamily();
    }

    @When("I invite (guest|registered) user with email (.+) to (my|random) family$")
    public void iInvitePersonWithEmailToMyFamily(String userType, String email, String whoseFamily) {
        OASFamily family = sharedData.getFamily();
        OASAccount inviter = new OASAccount()
                .id(UUID.randomUUID())
                .firstName("firstName")
                .lastName("lastName")
                .email(email);

        OASInvitationToFamily invitationToFamily;
        if (userType.equals("guest")) {
            invitationToFamily = new OASInvitationToFamily()
                    .guest(true)
                    .inviter(inviter)
                    .family(family)
                    .email(email);
        } else {
            OASAccount invitee = new OASAccount()
                    .id(UUID.randomUUID())
                    .firstName("inviteeFirstName")
                    .lastName("inviteeLastName")
                    .email(email);
            invitationToFamily = new OASInvitationToFamily()
                    .guest(false)
                    .inviter(inviter)
                    .family(family)
                    .invitee(invitee)
                    .invitationCode(UUID.randomUUID());
        }

        HttpEntity<?> entity =
                new HttpEntity<>(invitationToFamily, security.getSecurityHeaders());
        ResponseEntity<OASInvitationToFamily> responseEntity = testRestTemplate
                .exchange("/api/mail/v1/family-invitations", HttpMethod.POST, entity, OASInvitationToFamily.class);
        sharedData.setLastResponseEntity(responseEntity);
    }

    @Then("email with invitation is sent")
    public void emailWithInvitationIsSent() {
        HttpStatus lastResponseHttpStatus = sharedData.getLastResponseEntity().getStatusCode();
        assertThat(lastResponseHttpStatus.is2xxSuccessful()).isTrue();
    }
}
