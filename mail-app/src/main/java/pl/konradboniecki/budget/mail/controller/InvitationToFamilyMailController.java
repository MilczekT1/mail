package pl.konradboniecki.budget.mail.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.konradboniecki.budget.mail.service.InvitationService;
import pl.konradboniecki.budget.openapi.api.InvitationsApi;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;

import static pl.konradboniecki.budget.mail.controller.InvitationToFamilyMailController.BASE_PATH;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(BASE_PATH)
public class InvitationToFamilyMailController implements InvitationsApi {
    public static final String BASE_PATH = "/api/mail/v1";

    @Qualifier("ExistingUserInvitationService")
    private InvitationService registeredUserInvitationService;
    @Qualifier("NewUserInvitationService")
    private InvitationService guestUserInvitationService;

    @Override
    public ResponseEntity<Void> sendInvitationToFamily(OASInvitationToFamily invitationToFamily) throws Exception {
        if (invitationToFamily.getGuest()) {
            guestUserInvitationService.sendInvitation(invitationToFamily);
        } else {
            registeredUserInvitationService.sendInvitation(invitationToFamily);
        }
        return ResponseEntity.noContent().build();
    }
}
