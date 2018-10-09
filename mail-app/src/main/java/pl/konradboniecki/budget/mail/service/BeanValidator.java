package pl.konradboniecki.budget.mail.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
@NoArgsConstructor
@Service
public class BeanValidator {

    public void validateBean(Object object) throws IllegalArgumentException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        List<String> validationErrorMessages = new LinkedList<>();
        for (ConstraintViolation<Object> violation : violations) {
            String errorMsg = violation.getPropertyPath() + " " + violation.getMessage();
            log.error(violation.getPropertyPath() + " " + violation.getMessage());
            validationErrorMessages.add(errorMsg);
        }
        if (!validationErrorMessages.isEmpty()) {
            throw new IllegalArgumentException("Bean validation failed: " + validationErrorMessages);
        }
    }
}
