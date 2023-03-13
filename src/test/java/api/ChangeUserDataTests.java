package api;

import com.google.gson.Gson;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.http.UserClient;
import ru.yandex.praktikum.httpModel.User;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ChangeUserDataTests {

    private Gson gson = new Gson();
    private UserClient userClient = new UserClient();
    private User user = new User("email-Userwww@yandex.ru", "UserwwwPassword", "UserNamePavel");
    private User userWithNewData = new User("newEmail-User@yandex.ru", "NewUserPassword", "NewUserNameAndrei");

    @After
    public void deleteUser(){
        ValidatableResponse responseLogin = userClient.loginUser(userWithNewData);
        String loginBody = responseLogin.extract().body().asPrettyString();
        User newUser = gson.fromJson(loginBody, User.class);
        if (newUser.getAccessToken() != null) {
            userClient.deleteUser(newUser.getAccessToken());
        }
    }

    @Test
    @DisplayName("Изменение данных юзера с авторизацией")
    @Description("Изменение данных пользователя")
    public void editAuthUserDataTest(){
        ValidatableResponse responseCreate = userClient.createUser(user);
        String createBody = responseCreate.extract().body().asPrettyString();
        User createdUser = gson.fromJson(createBody, User.class);
        System.out.println(createBody);

        ValidatableResponse responseEdit = userClient.changeAuthUserData(userWithNewData, createdUser.getAccessToken());
        String editBody = responseEdit.extract().body().asPrettyString();

        //assertThat("\"Код ответа не соответствует 200\"", responseEdit.extract().statusCode(), equalTo(200));
        //assertThat("\"Error: Что-то пошло не так\"", editBody, containsString("\"success\": true,\n" +
        //        "    \"user\":"));
    }

    @Test
    @DisplayName("Изменение данных юзера без авторизации")
    @Description("Изменение данных неавторизованного пользователя")
    public void editNotAuthUserDataTest(){
        ValidatableResponse responseCreate = userClient.createUser(userWithNewData);
        String createBody = responseCreate.extract().body().asPrettyString();

        ValidatableResponse responseEdit = userClient.changeNotAuthUserData(user);
        String editBody = responseEdit.extract().body().asPrettyString();

        assertThat("\"Код ответа не соответствует 200\"", responseEdit.extract().statusCode(), equalTo(401));
        assertThat("\"Error: Что-то пошло не так\"", editBody, containsString("\"success\": false,\n" +
                "    \"message\": \"You should be authorised\""));
        assertNotEquals(createBody, editBody);
    }
}