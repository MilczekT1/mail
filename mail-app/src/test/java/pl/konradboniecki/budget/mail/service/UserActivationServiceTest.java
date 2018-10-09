package pl.konradboniecki.budget.mail.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASSignUpDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
public class UserActivationServiceTest {

    @MockBean
    private MailService mailService;
    @Autowired
    private BeanValidator beanValidator;
    private UserActivationService userActivationService;

    @BeforeAll
    public void setup() {
        userActivationService = new UserActivationService(mailService, beanValidator);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserActivationRequests")
    public void givenInvalidArguments_whenSendEmail_thenFailure(OASSignUpDetails testArg) {
        Throwable catchedException = catchThrowable(
                () -> userActivationService.sendSignUpConfirmation(testArg));
        assertThat(catchedException).isInstanceOf(IllegalArgumentException.class);
    }

    private static List<OASSignUpDetails> provideInvalidUserActivationRequests() {
        List<OASSignUpDetails> argList = new ArrayList<>();
        OASSignUpDetails arg1 = new OASSignUpDetails()
                .account(null)
                .activationCode(null);
        OASSignUpDetails arg2 = new OASSignUpDetails()
                .account(null)
                .activationCode(UUID.randomUUID());
        OASSignUpDetails arg3 = new OASSignUpDetails()
                .account(new OASAccount())
                .activationCode(null);
        OASSignUpDetails arg4 = new OASSignUpDetails()
                .account(new OASAccount().email("invalidEmail"))
                .activationCode(UUID.randomUUID());
        argList.add(null);
        argList.add(arg1);
        argList.add(arg2);
        argList.add(arg3);
        argList.add(arg4);
        return argList;
    }

    @Test
    public void givenValidArguments_whenSendEmail_thenDoesNotThrow() {
        OASAccount acc = new OASAccount();
        acc.setFirstName("kon");
        acc.setLastName("bon");
        acc.setId(UUID.randomUUID());
        acc.setEmail("john@doe.com");
        OASSignUpDetails signUpDetails = new OASSignUpDetails()
                .account(acc)
                .activationCode(UUID.randomUUID());

        doNothing().when(mailService)
                .sendMailToUserUsingTemplate(anyString(), anyString(), any(), anyMap());
        assertDoesNotThrow(() ->
                userActivationService.sendSignUpConfirmation(signUpDetails));
    }
}
