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

public class OrdersReceiveTests {

    private Gson gson = new Gson();
    private OrderClient orderClient = new OrderClient();
    private UserClient userClient = new UserClient();
    private User user = new User("email-Userwww@yandex.ru", "UserwwwPassword", "UserNamePavel");

    Order ingredients = new Order(Arrays.asList("61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c"));

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
    @DisplayName("Получение списка заказов авторизованным юзером")
    @Description("Получение заказов конкретного авторизованного юзера")
    public void receiveOrdersCountWithAuthTest(){
        userClient.createUser(user);

        ValidatableResponse responseLogin = userClient.loginUser(user);
        String loginBody = responseLogin.extract().body().asPrettyString();
        User newUser = gson.fromJson(loginBody, User.class);

        orderClient.createOrderByAuthUserWithIngredient(ingredients, newUser.getAccessToken());

        ValidatableResponse responseReceiveOrders = orderClient.getUserOrdersWithAuth(newUser.getAccessToken());
        String createOrderBody = responseReceiveOrders.extract().body().asPrettyString();
        int statusCode = responseReceiveOrders.extract().statusCode();

        assertThat("\"Код ответа не соответствует 200\"", statusCode, equalTo(200));
        assertThat("\"Error: Что-то пошло не так. Тело ответа не соответствует ожидаемому\"", createOrderBody, containsString("\"success\": true,\n" +
                "    \"orders\": "));
        assertThat("\"Error: Что-то пошло не так. Тело ответа не соответствует ожидаемому\"", createOrderBody, containsString("\"status\": \"done\""));
    }

    @Test
    @DisplayName("Получение списка заказов неавторизованным юзером")
    @Description("Получение заказов конкретного неавторизованного юзера")
    public void receiveOrdersCountWithoutAuthTest(){
        userClient.createUser(user);

        ValidatableResponse responseLogin = userClient.loginUser(user);
        String loginBody = responseLogin.extract().body().asPrettyString();
        User newUser = gson.fromJson(loginBody, User.class);

        orderClient.createOrderByAuthUserWithIngredient(ingredients, newUser.getAccessToken());

        ValidatableResponse responseReceiveOrders = orderClient.getUserOrdersWithoutAuth();
        String createOrderBody = responseReceiveOrders.extract().body().asPrettyString();
        int statusCode = responseReceiveOrders.extract().statusCode();

        assertThat("\"Код ответа не соответствует 401\"", statusCode, equalTo(401));
        assertThat("\"Error: Что-то пошло не так. Тело ответа не соответствует ожидаемому\"", createOrderBody, containsString("\"success\": false,\n" +
                "    \"message\": \"You should be authorised\""));
    }
}
