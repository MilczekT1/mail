package pl.konradboniecki.budget.mail.cucumber.commons;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tests.acceptance")
public class AcceptanceTestsProperties {

    private String baseUrl;
}
