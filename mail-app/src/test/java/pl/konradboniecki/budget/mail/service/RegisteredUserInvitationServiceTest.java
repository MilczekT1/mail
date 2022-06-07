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
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
class RegisteredUserInvitationServiceTest {

    @MockBean
    private MailService mailService;
    private RegisteredUserInvitationService registeredUserInvitationService;
    private Method createContextForTemplateMethod;
    @Autowired
    private BeanValidator beanValidator;

    @BeforeAll
    void setup() throws NoSuchMethodException {
        createContextForTemplateMethod = RegisteredUserInvitationService.class.getDeclaredMethod("createContextForTemplate", OASFamily.class, OASAccount.class, OASAccount.class, String.class);
        createContextForTemplateMethod.setAccessible(true);
        registeredUserInvitationService = new RegisteredUserInvitationService(mailService, beanValidator);
    }

    @Test
    void givenAnyInvitationCode_whenCheckInvitationCode_thenIsInvitationCodeOkMethodReturnTrue() {
        OASInvitationToFamily itf = new OASInvitationToFamily()
                .invitee(TestDataFactory.populateValidAccount())
                .family(TestDataFactory.populateValidFamily())
                .inviter(TestDataFactory.populateValidAccount())
                .invitationCode(UUID.randomUUID())
                .email("john@doe.com")
                .guest(false);
        assertDoesNotThrow(() -> registeredUserInvitationService.sendInvitation(itf));
    }

    @Test
    void givenArguments_whenCreateContext_thenReturnMap() throws Exception {
        OASAccount acc = new OASAccount();
        acc.setFirstName("testFirstName");
        acc.setLastName("testLastName");
        acc.setId(UUID.randomUUID());
        acc.setEmail("test@email.com");
        OASAccount owner = new OASAccount();
        owner.setFirstName("testFirstName");
        owner.setLastName("testLastName");
        owner.setId(UUID.randomUUID());
        owner.setEmail("test@email.com");
        String invitationCode = "invitationCodeInAnyFormat";
        OASFamily family = new OASFamily();
        family.setId(UUID.randomUUID());
        family.setTitle("testTitle");

        Map<String, String> map = (Map) createContextForTemplateMethod.invoke(registeredUserInvitationService, family, acc, owner, invitationCode);

        Assertions.assertAll(
                ()-> assertTrue(map.containsKey("recipient")),
                ()-> assertTrue(map.containsKey("familyTitle")),
                ()-> assertTrue(map.containsKey("ownersFirstName")),
                ()-> assertTrue(map.containsKey("ownersLastName")),
                ()-> assertTrue(map.containsKey("ownersEmail")),
                ()-> assertTrue(map.containsKey("invitationLink"))
        );
    }

    @ParameterizedTest
    @MethodSource("createInputMatrix")
    void givenInvalidArguments_whenCheckInput_thenIsInputOkMethodReturnFalse(OASFamily family, OASAccount account, OASAccount owner, UUID invitationCode, Boolean guest) {
        OASInvitationToFamily itf = new OASInvitationToFamily()
                .family(family)
                .invitee(account)
                .inviter(owner)
                .invitationCode(invitationCode)
                .guest(guest);
        assertThrows(IllegalArgumentException.class, () -> registeredUserInvitationService.sendInvitation(itf));
    }

    private static Stream<Arguments> createInputMatrix() {
        UUID invitationCode = UUID.randomUUID();
        OASAccount account = new OASAccount();
        OASAccount owner = new OASAccount();
        OASFamily family = new OASFamily();
        return Stream.of(
                Arguments.of(null, null, null, null, null),
                Arguments.of(family, null, null, null, null),
                Arguments.of(family, account, null, null, null),
                Arguments.of(null, account, null, null, null),
                Arguments.of(null, account, owner, null, null),
                Arguments.of(family, account, owner, null, null),
                Arguments.of(family, null, owner, null, null),
                Arguments.of(null, null, owner, null, null),
                Arguments.of(null, null, owner, invitationCode, null),
                Arguments.of(family, null, owner, invitationCode, null),
                Arguments.of(null, account, owner, invitationCode, null),
                Arguments.of(null, account, null, invitationCode, null),
                Arguments.of(family, account, null, invitationCode, null),
                Arguments.of(family, null, null, invitationCode, null),
                Arguments.of(null, null, null, invitationCode, null)
        );
    }
}
