package pl.konradboniecki.budget.mail.cucumber.commons;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;

import java.util.UUID;

@Data
@Component
public class SharedData {
    private ResponseEntity<?> lastResponseEntity;
    private static OASFamily family;

    public OASFamily getFamily() {
        return getOrCreateFamily();
    }

    public OASFamily getOrCreateFamily() {
        if (family != null) {
            return family;
        } else {
            return new OASFamily()
                    .id(UUID.randomUUID())
                    .title("testTitle");
        }
    }
}
