package com.letscoffee.Constant;

public class BaseClass {
    private String BaseUrl = "http://technorizen.co.in/coffee_app/webservice/";

    public static BaseClass get() {
        return new BaseClass();
    }

    public String AboutUs() {
        return "http://technorizen.co.in/coffee_app/about.html";
    }

    public String PrivacyPolicy() {
        return "http://technorizen.co.in/coffee_app/Privacy.html";
    }

    public String SignUp() {
        return BaseUrl.concat("signup");
    }

    public String Login() {
        return BaseUrl.concat("login");
    }

    public String forgotPassword() {
        return BaseUrl.concat("forgot_password");
    }

    public String verifyOtp() {
        return BaseUrl.concat("verify_otp");
    }

    public String ResendOtp() {
        return BaseUrl.concat("rsend_otp");
    }

    public String getProfile() {
        return BaseUrl.concat("get_profile");
    }

    public String getCategory() {
        return BaseUrl.concat("get_category");
    }

    public String getBanner() {
        return BaseUrl.concat("get_banner_list");
    }

    public String getNearbyCoffeeShop() {
        return BaseUrl.concat("get_nearby_coffe_shop");
    }

    public String getItemListByCatShopID() {
        return BaseUrl.concat("shop_list_according_catshop_id");
    }

    public String getMembership() {
        return BaseUrl.concat("get_membership_list");
    }

    public String searchShop() {
        return BaseUrl.concat("search_shop");
    }

    public String addCart() {
        return BaseUrl.concat("add_cart");
    }

    public String getCart() {
        return BaseUrl.concat("get_cart");
    }
    public String getCoupon() {
        return BaseUrl.concat("coupon_codes_list");
    }
    public String addPlaceorder() {
        return BaseUrl.concat("add_placeorder");
    }
    public String getMyOrder() {
        return BaseUrl.concat("get_my_order");
    }public String getMemberOrder() {
        return BaseUrl.concat("get_member_order");
    }public String addMembership() {
        return BaseUrl.concat("add_membership");
    }public String getOrderDetails() {
        return BaseUrl.concat("get_orderdetails");
    }public String addFavRestaurant() {
        return BaseUrl.concat("add_fav_restaurant");
    }public String getFavRestaurant() {
        return BaseUrl.concat("get_restaurant_fav");
    }public String notificationList() {
        return BaseUrl.concat("notification_list");
    }public String shop_list_according_cat_id() {
        return BaseUrl.concat("shop_list_according_cat_id");
    }
}
