package com.softwareengineering.pcstore;

public class Review {
    private String  name , rating , id , p_id ;

    public Review(String id , String name, String rating , String p_id) {

        this.name = name;
        this.rating = rating;
        this.id = id ;
        this.p_id = p_id ;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
