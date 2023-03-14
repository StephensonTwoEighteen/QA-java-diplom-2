package api;

import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.http.UserClient;
import ru.yandex.praktikum.httpModel.User;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserLoginTests {
    private Gson gson = new Gson();
    private UserClient userClient = new UserClient();
    private User user = new User("newEmail@yandex.ru", "bamBamBamPassword", "Pavel");
    private User notExistUser = new User("notExistEmail@yandex.ru", "notExistPassword", "notExistName");

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
    @DisplayName("Логин пользователя")
    public void loginExistUserTest(){
        userClient.createUser(user);
        ValidatableResponse response = userClient.loginUser(user);

        String loginResponse = response.extract().body().asPrettyString();

        assertThat("\"Error: Что-то пошло не так. Тело ответа не соответствует ожидаемому\"", loginResponse, containsString("\"success\": true"));
    }

    @Test
    @DisplayName("Логин пользователя с неверным логином и паролем")
    public void loginNotExistUserTest(){
        ValidatableResponse response = userClient.loginUser(notExistUser);

        String loginResponse = response.extract().body().asPrettyString();

        assertThat("\"Error: Что-то пошло не так. Тело ответа не соответствует ожидаемому\"", loginResponse, containsString("\"message\": \"email or password are incorrect\""));
    }
}
