package com.example.recipe_recommender.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String id;
    private String title;
    private List<String> cuisineTypes = new ArrayList<>();
    private String difficultyLevel;

    private String cuisineType1;
    private String cuisineType2;

    public Recipe() {
    }

    public Recipe(String id, String title, List<String> cuisineTypes, String difficultyLevel) {
        this.id = id;
        this.title = title;
        this.cuisineTypes = cuisineTypes;
        this.difficultyLevel = difficultyLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(List<String> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getCuisineType1() {
        return cuisineType1;
    }

    public void setCuisineType1(String cuisineType1) {
        this.cuisineType1 = cuisineType1;
    }

    public String getCuisineType2() {
        return cuisineType2;
    }

    public void setCuisineType2(String cuisineType2) {
        this.cuisineType2 = cuisineType2;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", cuisineTypes=" + cuisineTypes +
                ", difficultyLevel='" + difficultyLevel + '\'' +
                '}';
    }
}