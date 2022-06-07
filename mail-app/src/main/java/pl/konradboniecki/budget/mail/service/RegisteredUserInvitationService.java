package pl.konradboniecki.budget.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.mail.model.dto.Invitation;
import pl.konradboniecki.budget.mail.model.dto.UserInvitation;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("existingUserInvitationService")
public class RegisteredUserInvitationService implements InvitationService {
    private static final String INVITATION_TITLE = "Budget - Invitation to family";

    @Value("${budget.baseUrl.gateway}")
    private String gatewayUrl;
    private MailService mailService;
    private BeanValidator beanValidator;

    public RegisteredUserInvitationService(MailService mailService, BeanValidator beanValidator) {
        this.mailService = mailService;
        this.beanValidator = beanValidator;
    }

    @Override
    public void sendInvitation(OASInvitationToFamily itf) {
        UserInvitation userInvitation = UserInvitation.of(itf);
        validateInvitation(userInvitation);
        log.info("Sending mail with invitation to family with id: {}, to user with id: {} on behalf of user with id: {}", itf.getFamily().getId(), itf.getInvitee().getId(), itf.getInviter().getId());

        Map<String, String> ctxVariables = createContextForTemplate(itf.getFamily(), itf.getInvitee(), itf.getInviter(), itf.getInvitationCode().toString());

        mailService.sendMailToUserUsingTemplate(INVITATION_TITLE, MailTemplate.INVITE_FAMILY_OLD_USER, itf.getInvitee().getEmail(), ctxVariables);
    }

    @Override
    public void validateInvitation(Invitation invitationToFamily) {
        beanValidator.validateBean(invitationToFamily);
    }

    private Map<String, String> createContextForTemplate(OASFamily family, OASAccount account, OASAccount owner, String invitationCode) {
        Map<String, String> ctxVariables = new HashMap<>();
        ctxVariables.put("recipient", account.getFirstName() + " " + account.getLastName());
        ctxVariables.put("familyTitle", family.getTitle());
        ctxVariables.put("ownersFirstName", owner.getFirstName());
        ctxVariables.put("ownersLastName", owner.getLastName());
        ctxVariables.put("ownersEmail", owner.getEmail());
        ctxVariables.put("invitationLink", gatewayUrl + "/pl/konradboniecki/budget/family/invitations/" + family.getId() + "/addMember/" + account.getId() + "/" + invitationCode);
        return ctxVariables;
    }
}
