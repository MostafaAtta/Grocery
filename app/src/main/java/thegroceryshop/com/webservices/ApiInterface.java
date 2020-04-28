package thegroceryshop.com.webservices;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rohitg on 12/6/2016.
 */

public interface ApiInterface {

    @Headers("Content-Type:application/json")
    @POST("register")
    Call<ResponseBody> mRegisterRequest(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("check_mobile")
    Call<ResponseBody> mCheckMobileNoExistance(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("check_unique_number")
    Call<ResponseBody> mCheckMobileUnique(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("check_email_exist")
    Call<ResponseBody> mCheckEmailExistance(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("verify_otp")
    Call<ResponseBody> mSendOtpno(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("Login")
    Call<ResponseBody> loginRequest(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getCategory")
    Call<ResponseBody> mGetSildeMenuCategory(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getCategoryHome")
    Call<ResponseBody> mGetHomeCatDetails(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getCategoryNameNested")
    Call<ResponseBody> loadcategories(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getCategoryProduct")
    Call<ResponseBody> loadProducts(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("product_detail")
    Call<ResponseBody> loadProduct(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_country_list")
    Call<ResponseBody> getCountryList(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_city_list")
    Call<ResponseBody> getCityList(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_area_list")
    Call<ResponseBody> getAreaList(@Body Map<String, Object> request);

    @GET("geocode/json")
    Call<ResponseBody> getAddress(@Query("latlng") String latlng, @Query("sensor") boolean sensor, @Query("key") String apikey);

    @Headers("Content-Type:application/json")
    @POST("get_shipping_address")
    Call<ResponseBody> getShippingAddress(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("delete_user_address")
    Call<ResponseBody> deleteShippingAddress(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("update_user_address")
    Call<ResponseBody> editShippingAddress(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("sameBrand")
    Call<ResponseBody> loadSameBrandProducts(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getSimilarProduct")
    Call<ResponseBody> loadSsimilarProducts(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("update_password")
    Call<ResponseBody> updatePassword(@Body Map<String, Object> request);

    @Multipart
    @POST("update_profile")
    Call<ResponseBody> updateProfile(
            @Part MultipartBody.Part image,
            @Part("lang_id") RequestBody lang_id,
            @Part("user_id") RequestBody user_id,
            @Part("first_name") RequestBody first_name,
            @Part("last_name") RequestBody last_name,
            @Part("dob") RequestBody dob);

    @Headers("Content-Type:application/json")
    @POST("add_user_address")
    Call<ResponseBody> addUserAddress(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("ShoppingCartUpdate")
    Call<ResponseBody> updateCartData(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_date_time_order")
    Call<ResponseBody> loadExpressdDate(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_delivery_time_slot")
    Call<ResponseBody> loadScheduleTimeSlot(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("start_card_process")
    Call<ResponseBody> startCardProcess(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("tokenization")
    Call<ResponseBody> acceptCardFromPayMob(@Query("payment_token") String payment_token, @Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("save_card")
    Call<ResponseBody> saveCardAPI(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("card_list")
    Call<ResponseBody> getCardList(@Body Map<String, Object> request);


    //Apply Coupon code :- off100 , Bakery10
    @Headers("Content-Type:application/json")
    @POST("apply_coupon_code")
    Call<ResponseBody> applyCoupon(@Body Map<String, Object> request);


    @Headers("Content-Type:application/json")
    @POST("start_payment_process")
    Call<ResponseBody> startPaymentProcess(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("start_payment_process_new")
    Call<ResponseBody> startPaymentProcessNew(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("payments/pay")
    Call<ResponseBody> lastPaymentProcess1(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("final_payment_process")
    Call<ResponseBody> lastPaymentProcess(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("payment_responce_mobile")
    Call<ResponseBody> paymentResponse(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("order_reserved")
    Call<ResponseBody> reserveOrReleaseTimeSlot(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_shipping_deliver_price")
    Call<ResponseBody> getFreeShippingAmount(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_home_promotion_product")
    Call<ResponseBody> loadPromotionProducts(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("order_cod")
    Call<ResponseBody> placeCashOnDelivery(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("order_cod_credits")
    Call<ResponseBody> placeOrder(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("home_search_suggestion")
    Call<ResponseBody> loadSuggestions(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getOrderListUser")
    Call<ResponseBody> loadCurrentOrders(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getCompletedOrder")
    Call<ResponseBody> loadCompletedOrders(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getOrderDetail")
    Call<ResponseBody> loadOrderDetail(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_order_location")
    Call<ResponseBody> updateCaptainLocation(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("CancelOrder")
    Call<ResponseBody> cancelOrder(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("updateNotifications")
    Call<ResponseBody> updateNotificationPrefrences(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("delete_card")
    Call<ResponseBody> deleteCard(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("total_weight_init")
    Call<ResponseBody> getTotalWeight(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_user_profile")
    Call<ResponseBody> getUserProfile(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("product_barcode_detail")
    Call<ResponseBody> loadBarCodeProducts(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("changePassword")
    Call<ResponseBody> mChangePassword(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("notifyme")
    Call<ResponseBody> notifyMe(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("user_notifications")
    Call<ResponseBody> getNotificationsList(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("clear_notification")
    Call<ResponseBody> removeNotification(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_otp_registration")
    Call<ResponseBody> getOtpRegistration(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_home_promotion_product_filter")
    Call<ResponseBody> getPromotionFilter(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getCategoryProduct_filter")
    Call<ResponseBody> getCategoryFilter(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getArea")
    Call<ResponseBody> getArea(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("logoutUser")
    Call<ResponseBody> logout(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("scheduled_delivery")
    Call<ResponseBody> getFreeShippingCharges(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("delivery_charges")
    Call<ResponseBody> getShippingCharges(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("getUserCredit")
    Call<ResponseBody> getCredits(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("start_store_credit_process")
    Call<ResponseBody> startAddCreditProcess(@Body Map<String, Object> request);

    /* For HelpDesk*/
    @FormUrlEncoded
    @POST("./")
    Call<ResponseBody> createRequest(@Field("format") String format, @Field("OPERATION_NAME") String operationName, @Field("TECHNICIAN_KEY") String technicianKey, @Field("INPUT_DATA") String inputData);

    /* For HelpDesk*/
    @FormUrlEncoded
    @POST("./{request_id}/notes")
    Call<ResponseBody> addNotes(@Field("format") String format, @Field("OPERATION_NAME") String operationName, @Path("request_id") String request_id, @Field("TECHNICIAN_KEY") String technicianKey, @Field("INPUT_DATA") String inputData);

    @Headers("Content-Type:application/json")
    @POST("get_saved_wishlists")
    Call<ResponseBody> getSavedWishLists(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("create_wishlist")
    Call<ResponseBody> createWishList(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("wishlist_info")
    Call<ResponseBody> wishListInfo(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("remove_wishlist")
    Call<ResponseBody> removeWishList(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_my_savings")
    Call<ResponseBody> getMySaving(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_product_rate_reviews")
    Call<ResponseBody> getProductRateReview(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("submit_review_rating")
    Call<ResponseBody> submitReviewRating(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("submit_product_review_rating")
    Call<ResponseBody> submitProductReviewRating(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_coupon_from_barcode")
    Call<ResponseBody> getCouponFromBarcode(@Body Map<String, Object> request);

    @Headers("Content-Type:application/json")
    @POST("get_initial_data")
    Call<ResponseBody> loadInitialData(@Body Map<String, Object> request);
}
