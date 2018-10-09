package pl.konradboniecki.budget.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.mail.model.dto.GuestInvitation;
import pl.konradboniecki.budget.mail.model.dto.Invitation;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GuestUserInvitationService implements InvitationService {
    private static final String INVITATION_TITLE = "Budget - Invitation to family";

    @Value("${budget.baseUrl.gateway}")
    private String BASE_URL;
    private final MailService mailService;
    private final BeanValidator beanValidator;

    public GuestUserInvitationService(MailService mailService, BeanValidator beanValidator) {
        this.mailService = mailService;
        this.beanValidator = beanValidator;
    }

    @Override
    public void sendInvitation(OASInvitationToFamily itf) {
        GuestInvitation guestInvitation = GuestInvitation.of(itf);
        validateInvitation(guestInvitation);
        Map<String, String> ctxtVariables = createContextForTemplate(itf.getInviter(), itf.getFamily(), itf.getEmail());
        mailService.sendMailToUserUsingTemplate(INVITATION_TITLE, MailTemplate.INVITE_FAMILY_NEW_USER, itf.getEmail(), ctxtVariables);
        log.info("Mail with invitation to family with id: " + itf.getFamily().getId() + " has been sent to new user with email adress: " + itf.getEmail());
    }

    @Override
    public void validateInvitation(Invitation invitationToFamily) {
        beanValidator.validateBean(invitationToFamily);
    }

    private Map<String, String> createContextForTemplate(OASAccount owner, OASFamily family, String newMemberMail) {
        Map<String, String> ctxtVariables = new HashMap<>();
        ctxtVariables.put("recipient", newMemberMail);
        ctxtVariables.put("familyTitle", family.getTitle());
        ctxtVariables.put("ownersFirstName", owner.getFirstName());
        ctxtVariables.put("ownersLastName", owner.getLastName());
        ctxtVariables.put("ownersEmail", owner.getEmail());
        ctxtVariables.put("registerLink", BASE_URL + "/register");
        return ctxtVariables;
    }
}
