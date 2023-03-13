package ru.yandex.praktikum.httpModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Order {

    private List<String> ingredients;

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Order(){}

    public static Order addIngredients(){
        return new Order(
                new ArrayList<>(Arrays.asList("61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c"))
        );
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
