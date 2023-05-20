package pl.konradboniecki.budget.mail.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;

import java.util.UUID;

@Getter
@JsonDeserialize(builder = UserInvitation.UserInvitationBuilder.class)
@Builder(builderClassName = "UserInvitationBuilder", toBuilder = true)
public class UserInvitation implements Invitation {
    @Valid
    @NotNull
    private final OASAccount inviter;
    @Valid
    @NotNull
    private final OASAccount invitee;
    @Valid
    @NotNull
    private final OASFamily family;
    @NotNull
    private final Boolean guest;
    @NotNull
    private final UUID invitationCode;

    public static UserInvitation of(OASInvitationToFamily oasInvitationToFamily) {
        return builder()
                .inviter(oasInvitationToFamily.getInviter())
                .invitee(oasInvitationToFamily.getInvitee())
                .family(oasInvitationToFamily.getFamily())
                .guest(false)
                .invitationCode(oasInvitationToFamily.getInvitationCode())
                .build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserInvitationBuilder {
    }
}
