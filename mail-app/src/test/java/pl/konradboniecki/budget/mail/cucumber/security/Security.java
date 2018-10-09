package pl.konradboniecki.budget.mail.cucumber.security;

import org.springframework.http.HttpHeaders;

public interface Security {

    HttpHeaders getSecurityHeaders();

    void basicAuthentication();

    void unathorize();

}
