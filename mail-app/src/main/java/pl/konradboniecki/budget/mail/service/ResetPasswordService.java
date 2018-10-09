package pl.konradboniecki.budget.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASResetPasswordDetails;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ResetPasswordService {

    @Value("${budget.baseUrl.gateway}")
    private String BASE_URL;

    private final MailService mailService;
    private final BeanValidator beanValidator;

    public ResetPasswordService(MailService mailService, BeanValidator beanValidator) {
        this.mailService = mailService;
        this.beanValidator = beanValidator;
    }

    public void sendNewPasswordActivationLink(OASResetPasswordDetails resetPasswordDetails) throws BadRequestException {
        validateInput(resetPasswordDetails);
        OASAccount acc = resetPasswordDetails.getAccount();

        Map<String, String> contextVariables = new HashMap<>();
        contextVariables.put("recipient", acc.getFirstName() + " " + acc.getLastName());
        contextVariables.put("resetLink",
                BASE_URL + "/api/reset-password/" + acc.getId() + "/" + resetPasswordDetails.getResetCode());

        log.info("Mail with password reset link has been sent to " + acc.getEmail());
        mailService.sendMailToUserUsingTemplate("Budget - New Password Activation",
                MailTemplate.CONFIRMATION_NEW_PASSWORD, acc.getEmail(), contextVariables);
    }

    private void validateInput(OASResetPasswordDetails resetPasswordDetails) throws BadRequestException {
        beanValidator.validateBean(resetPasswordDetails);
        beanValidator.validateBean(resetPasswordDetails.getAccount());
    }
}
