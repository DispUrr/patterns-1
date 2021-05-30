import com.github.javafaker.Faker;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {

    @Data
    @RequiredArgsConstructor
    public static class User {
        private final String name;
        private final String phone;
        private final String city;

    }

    public static User getUserInfo() {
        Faker faker = new Faker(new Locale("ru"));
        return new User(
                faker.name().fullName(),
                faker.phoneNumber().phoneNumber(),
                faker.address().cityName()
        );
    }

}