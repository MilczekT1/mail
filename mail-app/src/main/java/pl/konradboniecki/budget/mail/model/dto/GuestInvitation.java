package pl.konradboniecki.budget.mail.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationToFamily;


@Getter
@JsonDeserialize(builder = GuestInvitation.GuestInvitationBuilder.class)
@Builder(builderClassName = "GuestInvitationBuilder", toBuilder = true)
public class GuestInvitation implements Invitation {
    @Valid
    @NotNull
    private final OASAccount inviter;
    @Valid
    @NotNull
    private final OASFamily family;
    @Email
    @NotNull
    private final String email;
    @NotNull
    private final Boolean guest;

    public static GuestInvitation of(OASInvitationToFamily oasInvitationToFamily) {
        return builder()
                .inviter(oasInvitationToFamily.getInviter())
                .family(oasInvitationToFamily.getFamily())
                .email(oasInvitationToFamily.getEmail())
                .guest(true)
                .build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class GuestInvitationBuilder {
    }
}
