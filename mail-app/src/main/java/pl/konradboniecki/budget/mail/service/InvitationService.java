package pl.konradboniecki.budget.mail.service;

import pl.konradboniecki.budget.mail.model.dto.Invitation;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;

public interface InvitationService {
    void sendInvitation(OASInvitationToFamily invitationToFamily);

    void validateInvitation(Invitation invitationToFamily);
}
