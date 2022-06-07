package pl.konradboniecki.budget.mail.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.konradboniecki.budget.mail.service.UserActivationService;
import pl.konradboniecki.budget.openapi.api.AccountsApi;
import pl.konradboniecki.budget.openapi.dto.model.OASSignUpDetails;

@Slf4j
@AllArgsConstructor
@RestController
public class UserActivationMailController implements AccountsApi {
    private final UserActivationService userActivationService;

    @Override
    public ResponseEntity<Void> sendSignUpConfirmation(OASSignUpDetails oaSSignUpDetails) throws Exception {
        userActivationService.sendSignUpConfirmation(oaSSignUpDetails);
        return ResponseEntity.noContent().build();
    }
}
