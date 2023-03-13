package ru.yandex.praktikum.http;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.httpModel.User;

import static io.restassured.RestAssured.given;

public class UserClient extends StellarClient{

    public final String CREATE_USER = "/api/auth/register";
    public final String LOGIN_USER = "/api/auth/login";
    public final String TOUCH_USER = "/api/auth/user";

    @Step("Создание юзера")
    public ValidatableResponse createUser(User user) {
        return given().spec(baseSpec())
                .body(user)
                .when()
                .post(CREATE_USER)
                .then();
    }

    @Step("Логин юзера")
    public ValidatableResponse loginUser(User user){
        return given().spec(baseSpec())
                .body(user)
                .when()
                .post(LOGIN_USER)
                .then();
    }

    @Step("Изменение данных авторизованного юзера")
    public ValidatableResponse changeAuthUserData(User user, String accessToken){
        return given().spec(baseSpec())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(TOUCH_USER)
                .then();
    }

    @Step("Изменение данных неавторизованного юзера")
    public ValidatableResponse changeNotAuthUserData(User user){
        return given().spec(baseSpec())
                .body(user)
                .when()
                .patch(TOUCH_USER)
                .then();
    }

    @Step("Удаление юзера")
    public ValidatableResponse deleteUser(String accessToken){
        return given().spec(baseSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(TOUCH_USER)
                .then();
    }
}
