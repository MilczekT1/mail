package pl.konradboniecki.budget.mail;

import org.springframework.boot.SpringApplication;
import pl.konradboniecki.chassis.ChassisApplication;

@ChassisApplication(scanBasePackages = "pl.konradboniecki.chassis.configuration.webserver", scanBasePackageClasses = MailServiceApp.class)
public class MailServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(MailServiceApp.class, args);
    }
}
