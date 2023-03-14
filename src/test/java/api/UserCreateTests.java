package api;

import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.http.UserClient;
import ru.yandex.praktikum.httpModel.User;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserCreateTests {
    private Gson gson = new Gson();
    private UserClient userClient = new UserClient();
    private User user = new User("newEmail@yandex.ru", "bamBamBamPassword", "Pavel");

    @After
    public void deleteUser(){
        ValidatableResponse responseLogin = userClient.loginUser(user);
        String loginBody = responseLogin.extract().body().asPrettyString();
        User newUser = gson.fromJson(loginBody, User.class);
            if (newUser.getAccessToken() != null) {
                userClient.deleteUser(newUser.getAccessToken());
            }
    }

    @Test
    @DisplayName("Создание уникального юзера")
    public void createUniqueUserTest(){
        ValidatableResponse response = userClient.createUser(user);

        String createBody = response.extract().body().asPrettyString();
        int statusCode = response.extract().statusCode();

        System.out.println(createBody);

        assertThat("\"Код ответа не соответствует 200\"", statusCode, equalTo(200));
        assertThat("\"Error: Что-то пошло не так. Тело ответа не соответствует ожидаемому\"", createBody, containsString("\"accessToken\": \"Bearer "));
    }

    @Test
    @DisplayName("Создание уже существующего юзера")
    public void createUniqueUserAgainTest(){
        ValidatableResponse response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        assertThat("\"Код ответа не соответствует 200\"", statusCode, equalTo(200));

        //Попытка регистрации юзера с теми же данными
        ValidatableResponse newResponse = userClient.createUser(user);
        int newStatusCode = newResponse.extract().statusCode();
        assertThat("\"Код ответа не соответствует 403\"", newStatusCode, equalTo(403));
    }

    @Test
    @DisplayName("Создание юзера с незаполненным именем")
    public void createUniqueUserWithoutNameTest(){
        User userWithoutName = new User("bambambamwefwef@yandex.ru", "bamBamPassword", "");

        ValidatableResponse response = userClient.createUser(userWithoutName);
        int statusCode = response.extract().statusCode();
        assertThat("\"Код ответа не соответствует 403\"", statusCode, equalTo(403));
    }
}