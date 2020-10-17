package com.letscoffee.Models;

import java.io.Serializable;

public class ModelMyOrder implements Serializable {
    String id,status,book_time,amount,shop_name,shop_image,preparing_time,ready_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBook_time() {
        return book_time;
    }

    public void setBook_time(String book_time) {
        this.book_time = book_time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_image() {
        return shop_image;
    }

    public void setShop_image(String shop_image) {
        this.shop_image = shop_image;
    }

    public String getPreparing_time() {
        return preparing_time;
    }

    public void setPreparing_time(String preparing_time) {
        this.preparing_time = preparing_time;
    }

    public String getReady_time() {
        return ready_time;
    }

    public void setReady_time(String ready_time) {
        this.ready_time = ready_time;
    }
}
