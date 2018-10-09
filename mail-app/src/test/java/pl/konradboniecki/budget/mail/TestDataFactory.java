package pl.konradboniecki.budget.mail;

import com.github.javafaker.Faker;
import pl.konradboniecki.budget.openapi.dto.model.OASAccount;
import pl.konradboniecki.budget.openapi.dto.model.OASFamily;

import java.util.UUID;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    public static OASAccount populateValidAccount() {
        return new OASAccount()
                .id(UUID.randomUUID())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress());
    }

    public static OASFamily populateValidFamily() {
        return new OASFamily()
                .id(UUID.randomUUID())
                .title(faker.book().title());
    }

}
