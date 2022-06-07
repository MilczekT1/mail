package pl.konradboniecki.budget.mail.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.konradboniecki.budget.mail.service.ResetPasswordService;
import pl.konradboniecki.budget.openapi.api.PasswordsApi;
import pl.konradboniecki.budget.openapi.dto.model.OASResetPasswordDetails;

@Slf4j
@AllArgsConstructor
@RestController
public class ResetPasswordMailController implements PasswordsApi {

    private final ResetPasswordService resetPasswordService;

    @Override
    public ResponseEntity<Void> sendNewPasswordConfirmation(OASResetPasswordDetails oaSResetPasswordDetails) throws Exception {
        resetPasswordService.sendNewPasswordActivationLink(oaSResetPasswordDetails);
        return ResponseEntity.noContent().build();
    }
}
