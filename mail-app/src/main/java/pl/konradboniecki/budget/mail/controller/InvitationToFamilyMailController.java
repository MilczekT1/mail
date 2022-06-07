package pl.konradboniecki.budget.mail.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.konradboniecki.budget.mail.service.InvitationService;
import pl.konradboniecki.budget.openapi.api.InvitationsApi;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;

@Slf4j
@AllArgsConstructor
@RestController
public class InvitationToFamilyMailController implements InvitationsApi {

    @Qualifier("existingUserInvitationService")
    private InvitationService registeredUserInvitationService;
    @Qualifier("newUserInvitationService")
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
