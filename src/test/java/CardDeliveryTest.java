import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    private DataGenerator.User user;

    private final DataGenerator dataGenerator = new DataGenerator();

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        user = DataGenerator.getUserInfo();

    }

    // данные корректны, встреча запланирована
    @Test
    void shouldTestDeliveryCardFirst() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(dataGenerator.generateDate(3));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(byText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=success-notification] .notification__content").shouldHave(exactText("Встреча успешно запланирована на " + dataGenerator.generateDate(3)));
    }
    // данные корректны, успешное перепланирование
    @Test
    void shouldTestDeliveryCardSameDate() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(dataGenerator.generateDate(3));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(byText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content").shouldHave(exactText("Встреча успешно запланирована на " + dataGenerator.generateDate(3)));
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        String date1 = LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=date] input").setValue(dataGenerator.generateDate(4));;
        $$("button").find(exactText("Запланировать")).click();
        $(byText("Необходимо подтверждение")).shouldBe(visible);
        $$("button").find(exactText("Перепланировать")).click();
        $(byText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content").shouldHave(exactText("Встреча успешно запланирована на " + dataGenerator.generateDate(4)));
    }

    // Не заполнено ФИ
    @Test
    void shouldTestDeliveryCardWithoutName() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(dataGenerator.generateDate(3));
        $("[data-test-id=name] input").setValue("");
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=name] .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }
    // Не заполнен номер
    @Test
    void shouldTestDeliveryCardWithoutPhone() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(dataGenerator.generateDate(3));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue("");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=phone] .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
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
        $("[data-test-id=date] .input__sub").shouldHave(exactText("Неверно введена дата"));
    }
    // Некорректный город, остальные поля заполнены корректно
    @Test
    void shouldTestDeliveryCardIncorrectCity() {
        $("[data-test-id=city] input").setValue("Минск");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(dataGenerator.generateDate(3));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=city] .input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }
    // Дата в прошлом
    @Test
    void shouldTestDeliveryCardDateInPast() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        $("[data-test-id=date] input").setValue(dataGenerator.generateDate(-3));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=date] .input__sub").shouldHave(exactText("Заказ на выбранную дату невозможен"));
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
        $("[data-test-id=agreement] .checkbox__text")
                .shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }


}