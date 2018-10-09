package pl.konradboniecki.budget.mail.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASSignUpDetails;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserActivationService {
    private static final String SIGN_UP_CONFIRMATION_TITLE = "Budget - Sign up completed";

    @Value("${budget.baseUrl.gateway}")
    private String BASE_URL;
    private final MailService mailService;
    private final BeanValidator beanValidator;

    public UserActivationService(MailService mailService, BeanValidator beanValidator) {
        this.mailService = mailService;
        this.beanValidator = beanValidator;
    }

    public void sendSignUpConfirmation(OASSignUpDetails signUpDetails) throws BadRequestException {
        validateInput(signUpDetails);
        OASAccount acc = signUpDetails.getAccount();

        Map<String, String> contextVariables = new HashMap<>();
        contextVariables.put("recipient", acc.getFirstName() + " " + acc.getLastName());
        contextVariables.put("activationLink",
                BASE_URL + "/api/account-mgt/v1/accounts/" + acc.getId() + "/activation-codes/" + signUpDetails.getActivationCode());
        log.info("Attempting to send activation link to: " + acc.getEmail());
        mailService.sendMailToUserUsingTemplate(SIGN_UP_CONFIRMATION_TITLE,
                MailTemplate.CONFIRMATION_SIGN_UP, acc.getEmail(), contextVariables);
    }

    private void validateInput(@NonNull OASSignUpDetails oasSignUpDetails) throws BadRequestException {
        beanValidator.validateBean(oasSignUpDetails);
        beanValidator.validateBean(oasSignUpDetails.getAccount());
    }
}
