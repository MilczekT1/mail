package pl.konradboniecki.budget.mail.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.mail.TestDataFactory;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
class GuestUserInvitationServiceTest {

    @MockBean
    private MailService mailService;
    private GuestUserInvitationService guestUserInvitationService;
    private Method createContextForTemplateMethod;
    @Autowired
    private BeanValidator beanValidator;

    @BeforeAll
    void setup() throws NoSuchMethodException {
        createContextForTemplateMethod = GuestUserInvitationService.class.getDeclaredMethod("createContextForTemplate", OASAccount.class, OASFamily.class, String.class);
        createContextForTemplateMethod.setAccessible(true);

        guestUserInvitationService = new GuestUserInvitationService(mailService, beanValidator);
    }

    @ParameterizedTest
    @MethodSource("blankStrings")
    void givenBlankEmail_whenCheckEmail_thenThrow(String email) {
        OASInvitationToFamily itf = new OASInvitationToFamily()
                .inviter(new OASAccount())
                .family(new OASFamily())
                .email(email);
        assertThrows(IllegalArgumentException.class, () -> guestUserInvitationService.sendInvitation(itf));
    }

    static Stream<String> blankStrings() {
        return Stream.of("", " ", null);
    }

    @Test
    void givenArguments_whenCreateContext_thenReturnMap() throws Exception {
        OASAccount acc = new OASAccount();
        acc.setFirstName("testFirstName");
        acc.setLastName("testLastName");
        acc.setId(UUID.randomUUID());
        acc.setEmail("test@email.com");
        String email = "test@email.com";
        OASFamily family = new OASFamily();
        family.setId(UUID.randomUUID());
        family.setTitle("testTitle");

        Map<String, String> map = (Map<String, String>) createContextForTemplateMethod.invoke(guestUserInvitationService, (acc), family, email);
        Assertions.assertAll(
                ()-> assertTrue(map.containsKey("recipient")),
                ()-> assertTrue(map.containsKey("familyTitle")),
                ()-> assertTrue(map.containsKey("ownersFirstName")),
                ()-> assertTrue(map.containsKey("ownersLastName")),
                ()-> assertTrue(map.containsKey("ownersEmail")),
                ()-> assertTrue(map.containsKey("registerLink"))
        );
    }

    @Test
    void givenValidArguments_whenSendEmailAndSuccess_thenReturnTrue() {
        doNothing().when(mailService)
                .sendMailToUserUsingTemplate(anyString(), anyString(), anyString(), anyMap());
        OASInvitationToFamily itf = new OASInvitationToFamily()
                .guest(true)
                .family(TestDataFactory.populateValidFamily())
                .inviter(TestDataFactory.populateValidAccount())
                .email("john@doe.com");
        assertDoesNotThrow(() -> guestUserInvitationService.sendInvitation(itf));
    }

    @ParameterizedTest
    @MethodSource("createAccountFamilyEmailMatrix")
    void givenInvalidArguments_whenCheckInput_thenThrow(OASAccount account, OASFamily family, String email) {
        OASInvitationToFamily itf = new OASInvitationToFamily()
                .inviter(account)
                .family(family)
                .email(email);
        assertThrows(IllegalArgumentException.class, () -> guestUserInvitationService.sendInvitation(itf));
    }

    private static Stream<Arguments> createAccountFamilyEmailMatrix(){
        String email = "emailInAnyFormat";
        OASAccount acc = new OASAccount();
        OASFamily family = new OASFamily();
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(null, null, email),
                Arguments.of(acc, null, null),
                Arguments.of(acc, null, email),
                Arguments.of(acc, family, null),
                Arguments.of(null, family, null),
                Arguments.of(null, family, email)
        );
    }
}
