package pl.konradboniecki.budget.mail.cucumber.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.konradboniecki.budget.mail.service.MailService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Configuration
@EnableConfigurationProperties(pl.konradboniecki.budget.mail.cucumber.commons.AcceptanceTestsProperties.class)
public class Config {

    @Value("${local.server.port:0}")
    int localServerPort;
    @Autowired
    private AcceptanceTestsProperties acceptanceTestsProperties;

    @Bean
    @Profile("test")
    public TestRestTemplate testRestTemplateOnMavenBuild() {
        log.info("SETUP -> Initializing TestRestTemplate on maven build");
        String rootUri = "http://localhost:" + localServerPort;
        TestRestTemplate testRestTemplate = new TestRestTemplate(new RestTemplateBuilder().rootUri(rootUri));
        testRestTemplate.getRestTemplate().getInterceptors().add(new CucumberInterceptor());
        return testRestTemplate;
    }

    @Bean
    @Profile("acceptance-tests")
    public TestRestTemplate testRestTemplateOnDeployment() {
        log.info("SETUP -> Initializing TestRestTemplate on deployment");
        String rootUri = acceptanceTestsProperties.getBaseUrl();
        assertThat(rootUri).isNotNull();

        TestRestTemplate testRestTemplate = new TestRestTemplate(new RestTemplateBuilder().rootUri(rootUri));
        testRestTemplate.getRestTemplate().getInterceptors().add(new CucumberInterceptor());
        return testRestTemplate;
    }

    @Bean
    @ConditionalOnProperty(value = "tests.acceptance.mockMail", havingValue = "true")
    MailService mailService() {
        log.info("Mocking MailService");
        return new MailService(null, null) {
            @Override
            public void sendMailToUserUsingTemplate(String title, String templateName, String destination, Map<String, String> contextVariables) {
                return;
            }
        };
    }
}
