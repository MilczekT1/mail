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
import pl.konradboniecki.budget.openapi.dto.model.OASResetPasswordDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
public class ResetPasswordServiceTest {

    private ResetPasswordService resetPasswordService;
    @MockBean
    private MailService mailService;
    @Autowired
    private BeanValidator beanValidator;

    @BeforeAll
    public void setup() throws NoSuchMethodException {
        resetPasswordService = new ResetPasswordService(mailService, beanValidator);
    }

    @Test
    public void givenValidResetCode_whenCheckResetCode_thenDontThrow() {
        OASAccount account = new OASAccount()
                .lastName("lastName")
                .firstName("firstName")
                .id(UUID.randomUUID())
                .email("email@email.com");

        OASResetPasswordDetails resetPasswordDetails = new OASResetPasswordDetails()
                .account(account)
                .resetCode(UUID.randomUUID());

        assertDoesNotThrow(() -> resetPasswordService.sendNewPasswordActivationLink(resetPasswordDetails));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidResetPasswordRequests")
    public void givenInvalidArguments_whenSendEmail_thenFailure(OASResetPasswordDetails testArg) {
        assertThrows(IllegalArgumentException.class,
                () -> resetPasswordService.sendNewPasswordActivationLink(testArg));
    }

    private static List<OASResetPasswordDetails> provideInvalidResetPasswordRequests() {
        List<OASResetPasswordDetails> argList = new ArrayList<>();
        OASResetPasswordDetails arg1 = new OASResetPasswordDetails()
                .account(null)
                .resetCode(null);
        OASResetPasswordDetails arg2 = new OASResetPasswordDetails()
                .account(null)
                .resetCode(UUID.randomUUID());
        OASResetPasswordDetails arg3 = new OASResetPasswordDetails()
                .account(new OASAccount())
                .resetCode(null);
        argList.add(arg1);
        argList.add(arg2);
        argList.add(arg3);
        return argList;
    }

    @Test
    public void givenValidArguments_whenSendEmail_thenSuccess() {
        OASAccount acc = new OASAccount()
                .firstName("kon")
                .lastName("bon")
                .id(UUID.randomUUID())
                .email("email@email.com");
        OASResetPasswordDetails resetPasswordDetails = new OASResetPasswordDetails()
                .account(acc)
                .resetCode(UUID.randomUUID());

        doNothing().when(mailService)
                .sendMailToUserUsingTemplate(anyString(), anyString(), any(), anyMap());
        assertDoesNotThrow(() ->
                resetPasswordService.sendNewPasswordActivationLink(resetPasswordDetails));
    }
}
