package api;

import com.google.gson.Gson;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.http.OrderClient;
import ru.yandex.praktikum.http.UserClient;
import ru.yandex.praktikum.httpModel.Order;
import ru.yandex.praktikum.httpModel.User;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrderCreateTests {
    private Gson gson = new Gson();
    private OrderClient orderClient = new OrderClient();
    private UserClient userClient = new UserClient();
    private User user = new User("email-Userwww@yandex.ru", "UserwwwPassword", "UserNamePavel");

    Order ingredients = new Order(Arrays.asList("61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c"));
    Order wrongHashIngredients = new Order(Arrays.asList("wrongHashd1f82001bdaaa6e", "wrongHashd1f82001bdaaa6c"));


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
    @DisplayName("Заказ авторизованного юзера")
    @Description("Заказ в существующими ингредиентами")
    public void createOrderWithAuthAndIngredientsTest(){
        userClient.createUser(user);
        ValidatableResponse responseLogin = userClient.loginUser(user);
        String loginBody = responseLogin.extract().body().asPrettyString();
        User newUser = gson.fromJson(loginBody, User.class);

        ValidatableResponse responseCreateOrder = orderClient.createOrderByAuthUserWithIngredient(ingredients, newUser.getAccessToken());
        String createOrderBody = responseCreateOrder.extract().body().asPrettyString();
        int statusCode = responseCreateOrder.extract().statusCode();

        assertThat("\"Код ответа не соответствует 200\"", statusCode, equalTo(200));
        assertThat("\"Error: Что-то пошло не так. Тело ответа не соответствует ожидаемому\"", createOrderBody, containsString("\"success\": true"));
        assertThat("\"Error: Что-то пошло не так. Тело ответа не соответствует ожидаемому\"", createOrderBody, containsString("\"price\": "));
    }

    @Test
    @DisplayName("Заказ неавторизованного юзера")
    @Description("Заказ с существующими ингредиентами юзером без авторизации")
    public void createOrderWithoutAuthAndIngredientsTest(){
        userClient.createUser(user);

        ValidatableResponse responseCreateOrder = orderClient.createOrderByNotAuthUserWithIngredients(ingredients);
        int statusCode = responseCreateOrder.extract().statusCode();

        assertThat("\"Код ответа не соответствует 401\"", statusCode, equalTo(401));
    }

    @Test
    @DisplayName("Заказ авторизованного юзера без ингредиентов")
    @Description("Заказ без существующих ингредиентов")
    public void createOrderWithAuthAndWithoutIngredientsTest(){
        userClient.createUser(user);
        ValidatableResponse responseLogin = userClient.loginUser(user);
        String loginBody = responseLogin.extract().body().asPrettyString();
        User newUser = gson.fromJson(loginBody, User.class);

        ValidatableResponse responseCreateOrder = orderClient.createOrderByAuthUserWithoutIngredients(newUser.getAccessToken());
        String createOrderBody = responseCreateOrder.extract().body().asPrettyString();
        int statusCode = responseCreateOrder.extract().statusCode();

        assertThat("\"Код ответа не соответствует 400\"", statusCode, equalTo(400));
        assertThat("\"Error: Что-то пошло не так. Тело ответа не соответствует ожидаемому\"", createOrderBody, containsString("\"success\": false,\n" +
                "    \"message\": \"Ingredient ids must be provided\""));
    }

    @Test
    @DisplayName("Заказ авторизованного юзера с невалидным хешем ингредиентов")
    @Description("Заказ c несуществующими ингредиентами")
    public void createOrderWithAuthAndWrongIngredientsTest(){
        userClient.createUser(user);
        ValidatableResponse responseLogin = userClient.loginUser(user);
        String loginBody = responseLogin.extract().body().asPrettyString();
        User newUser = gson.fromJson(loginBody, User.class);

        ValidatableResponse responseCreateOrder = orderClient.createOrderByAuthUserWithWrongIngredients(wrongHashIngredients, newUser.getAccessToken());
        int statusCode = responseCreateOrder.extract().statusCode();

        assertThat("\"Код ответа не соответствует 500\"", statusCode, equalTo(500));
    }


}
