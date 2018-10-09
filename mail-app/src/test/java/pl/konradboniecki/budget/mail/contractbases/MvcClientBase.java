package pl.konradboniecki.budget.mail.contractbases;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.mail.MailServiceApp;
import pl.konradboniecki.budget.mail.service.MailService;
import pl.konradboniecki.budget.mail.service.RegisteredUserInvitationService;
import pl.konradboniecki.budget.mail.service.UserActivationService;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASSignUpDetails;

import java.util.UUID;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = MailServiceApp.class,
        webEnvironment = WebEnvironment.RANDOM_PORT
)
public class MvcClientBase {

    @LocalServerPort
    int port;
    @MockBean
    protected UserActivationService userActivationService;
    @MockBean
    protected MailService mailService;
    @Autowired
    private RegisteredUserInvitationService registeredUserInvitationService;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost:" + this.port;
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));
        OASAccount existingAccount = new OASAccount()
                .id(UUID.fromString("bdde2539-37fd-4e06-897d-2c145ca4afba"))
                .firstName("testFirstName")
                .lastName("testLastName")
                .email("test@mail.com");
        UUID activationCode = UUID.fromString("29431ce1-8282-4489-8dd9-50f91e4c5653");
        OASSignUpDetails signUpDetails = new OASSignUpDetails()
                .account(existingAccount)
                .activationCode(activationCode);
        doNothing()
                .when(userActivationService).sendSignUpConfirmation(refEq(signUpDetails));
        doNothing()
                .when(mailService).sendMailToUserUsingTemplate(any(), any(), any(), any());
    }
}
