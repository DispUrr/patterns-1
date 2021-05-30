import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    private DataGenerator.User user;

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        user = DataGenerator.getUserInfo();
    }

    // данные корректны, встреча запланирована
    @Test
    void shouldTestDeliveryCardFirst() {
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(byText("Успешно!")).waitUntil(visible, 15000);
        boolean exists = $("[data-test-id=success-notification] .notification__content").exists();
    }
    // данные корректны, успешное перепланирование
    @Test
    void shouldTestDeliveryCardSameDate() {
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(byText("Успешно!")).waitUntil(visible, 15000);
        boolean exists = $("[data-test-id=success-notification].notification__content").exists();
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        String date1 = LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=date] input").setValue(date1);
        $$("button").find(exactText("Запланировать")).click();
        $(byText("Необходимо подтверждение")).shouldBe(visible);
        $$("button").find(exactText("Перепланировать")).click();
        $(byText("Успешно!")).waitUntil(visible, 15000);
        boolean exists1 = $("[data-test-id=success-notification].notification__content").exists();
    }
    // поля не заполнены, чек-бокс не отмечен
    @Test
    void shouldTestDeliveryCardWithoutDataAndAgreement() {
        $("[data-test-id=city] input").setValue("");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue("");
        $("[data-test-id=name] input").setValue("");
        $("[data-test-id=phone] input").setValue("");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        boolean exists = $("[data-test-id=city].input_invalid").exists();
    }
    // Не заполнено ФИ
    @Test
    void shouldTestDeliveryCardWithoutName() {
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("");
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        boolean exists = $("[data-test-id=name].input_invalid").exists();
    }
    // Не заполнен номер
    @Test
    void shouldTestDeliveryCardWithoutPhone() {
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue("");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        boolean exists = $("[data-test-id=phone].input_invalid").exists();
    }
    // Не заполнена дата
    @Test
    void shouldTestDeliveryCardWithoutDate() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue("");
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        boolean exists = $("[data-test-id=date] .input_invalid").exists();
    }
    // Некорректный город, остальные поля заполнены корректно
    @Test
    void shouldTestDeliveryCardIncorrectCity() {
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=city] input").setValue("Минск");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        boolean exists = $("[data-test-id=city].input_invalid").exists();
    }
    // Дата в прошлом
    @Test
    void shouldTestDeliveryCardDateInPast() {
        String date = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=date] .input_invalid").shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }
    // Чек-бокс не отмечен
    @Test
    void shouldTestDeliveryCardWithoutAgreement() {
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        boolean exists = $("[data-test-id=agreement].input_invalid").exists();
    }


}