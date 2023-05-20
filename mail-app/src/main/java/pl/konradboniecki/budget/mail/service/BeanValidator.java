package pl.konradboniecki.budget.mail.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
@NoArgsConstructor
@Service
public class BeanValidator {

    public void validateBean(Object object) throws IllegalArgumentException {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
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
}
