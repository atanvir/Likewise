package com.likewise.Retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.FontResourcesParserCompat;

import com.google.android.gms.common.Feature;
import com.google.firebase.encoders.annotations.Encodable;
import com.likewise.Model.CommonLanguageModel;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.CreateGameResponse;
import com.likewise.Model.CreateRoomResponse;
import com.likewise.Model.PlayGameResponseModel;
import com.likewise.Model.SignupModel;

import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServicesInterface {

    @POST(AppConstants.SIGNUP)
    Call<CommonModelDataObject> signup(@Body SignupModel model);

    @FormUrlEncoded
    @POST(AppConstants.LOGIN)
    Call<CommonModelDataObject> login(@Field("email") String email,
                                      @Field("password") String password,
                                      @Field("deviceType") String deviceType,
                                      @Field("deviceToken") String deviceToken);

    @FormUrlEncoded
    @POST(AppConstants.SOCIAL_LOGIN)
    Call<CommonModelDataObject> socialLogin(@Field("userId") String userId,
                                            @Field("socialId") String socialId,
                                            @Field("socialType") String socialType,
                                            @Field("deviceToken") String deviceToken,
                                            @Field("deviceType") String deviceType,
                                            @Field("name") String name,
                                            @Field("profilePic") String profilePic,
                                            @Field("email") String email,
                                            @Field("mobileNumber") String mobileNumber,
                                            @Field("languageCode") Object langCode[],
                                            @Field("type") String type,
                                            @Field("mode") String mode);

    @FormUrlEncoded
    @POST(AppConstants.FORGOT_PASSWORD)
    Call<CommonModelDataObject> forgotPassword(@Field("email") String email);


    @FormUrlEncoded
    @POST(AppConstants.CHECK_EMAIL)
    Call<CommonModelDataObject> checkEmail(@Field("email") String email,
                                           @Field("username") String username);


    @GET(AppConstants.LIST_LANGUAGE)
    Call<CommonLanguageModel> listLanguage(@Query("language") String langauge);

    @POST(AppConstants.GUEST_USER)
    Call<CommonModelDataObject> guestLogin(@Body SignupModel model);

    @GET(AppConstants.VIEW_PROFILE)
    Call<CommonModelDataObject> getUserDetail();

    @Multipart
    @POST(AppConstants.UPDATE_PROFILE)
    Call<CommonModelDataObject> updateUserProfile(@Part MultipartBody.Part profilePic, @PartMap Map<String,RequestBody> allData);

    @FormUrlEncoded
    @POST(AppConstants.CHECK_SOCIAL)
    Call<CommonModelDataObject> checkSocalId(@Field("socialId") String socialId,
                                             @Field("deviceToken") String deviceToken,
                                             @Field("deviceType") String deviceType);

    @POST(AppConstants.LOGOUT)
    Call<CommonModelDataObject> logout();

    @POST(AppConstants.DELETE_ACCOUNT)
    Call<CommonModelDataObject> deleteUserAcount();

    @GET(AppConstants.LIST_FAQ)
    Call<CommonModelDataObject> listFaq();

    @Multipart
    @POST(AppConstants.ADD_FEEDBACK)
    Call<CommonModelDataObject> addFeedback(@Part MultipartBody.Part screenShot, @PartMap Map<String,RequestBody> allData);


    @GET(AppConstants.LIST_PRIVACY_POLICY)
    Call<CommonModelDataObject> listPrivacyPolicy();

    @FormUrlEncoded
    @POST(AppConstants.NOTIFICATION_UPDATE)
    Call<CommonModelDataObject> notificationUpdate(@Field("notification") boolean notification);


    @FormUrlEncoded
    @POST(AppConstants.CREATE_GAME_DATA)
    Call<CommonModelDataObject> createGame(@Field("type") String type);

    @GET(AppConstants.PUBLIC_GALLERY_IMAGES)
    Call<CommonModelDataList> listImages();

    @Multipart
    @POST(AppConstants.GAME_CREATE)
    Call<CreateGameResponse> gameCreate(@Part MultipartBody.Part image[], @PartMap Map<String,RequestBody> allData);

    @FormUrlEncoded
    @POST(AppConstants.ROOM_CREATE)
    Call<CreateRoomResponse> roomCreate(@Header("token") String token,
                                        @Field("receiver_id") String receiver_id,
                                        @Field("sender_id") String sender_id,
                                        @Field("game_id") String game_id,
                                        @Field("type") String type,
                                        @Field("languageCode") String languageCode,
                                        @Field("gameType") String gameType);

    @FormUrlEncoded
    @POST(AppConstants.GAME_DETAILS)
    Call<CommonModelDataObject> gameDetail(@Field("game_id") String game_id,@Field("receiver_id") String receiver_id);

    @FormUrlEncoded
    @POST(AppConstants.CHAT_HISTORY)
    Call<CommonModelDataObject> getChatData(@Field("room_id") String room_id,@Field("receiver_id") String receiver_id);

    @FormUrlEncoded
    @POST(AppConstants.GAME_OVER)
    Call<CommonModelDataObject> gameOver(@Field("room_id") String room_id,
                                         @Field("receiver_id") String receiver_id,
                                         @Field("sender_id") String sender_id,
                                         @Field("coin") String coin);

    @FormUrlEncoded
    @POST(AppConstants.GAME_LIST)
    Call<CommonModelDataList> getChatUserList(@Field("status") boolean status);


    @GET(AppConstants.NOTIFICATION_LIST)
    Call<CommonModelDataObject> getNotifiactionList();

    @FormUrlEncoded
    @POST(AppConstants.FIND_PATNER)
    Call<PlayGameResponseModel> getFindPatner(@Field("languageCode") String languageCode, @Field("type") String type);

    @FormUrlEncoded
    @POST(AppConstants.RANDOM_CARD)
    Call<CommonModelDataObject> countCardValue(@Field("sender_id") String sender_id,
                                               @Field("receiver_id") String receiver_id,
                                               @Field("Data") List<String> imgList);

    @FormUrlEncoded
    @POST(AppConstants.MY_MATCHES)
    Call<CommonModelDataList> myMatch(@Field("gameType") String gameType);

    @GET(AppConstants.OTHER_USER_DETAILS)
    Call<CommonModelDataObject> getOtherUserDatils(@Path("id") String id);

    @FormUrlEncoded
    @POST(AppConstants.LEADERBOARD)
    Call<CommonModelDataList> leaderboard(@Field("id") String id);

    @GET(AppConstants.GET_COINS_LIST)
    Call<CommonModelDataObject> getCoins();

    @GET(AppConstants.DAILY_COINS)
    Call<CommonModelDataObject> checkDailyCoins();

    @FormUrlEncoded
    @POST(AppConstants.ADD_COINS)
    Call<CommonModelDataObject> addCoins(@Field("coin") Integer coin);

    @FormUrlEncoded
    @POST(AppConstants.CHECK_COIN_DEDUCT)
    Call<CommonModelDataObject> checkCoinDeduct(@Field("_id") String id,@Field("game_id") String game_id);

    @FormUrlEncoded
    @POST(AppConstants.DAILY_COIN_COLLECT)
    Call<CommonModelDataObject> collectCoin(@Field("coin") Integer coin,@Field("type") String type);

    @FormUrlEncoded
    @POST(AppConstants.INVITE_FRIEND)
    Call<CommonModelDataObject> inviteFriend(@Field("userId") String userId, @Field("receiverId") String receiverId, @Field("coins") String coins);

    @GET("{socialId}/friends?access_token=682033362436149|9Rw8aWVmtiSsBrPcR-pMTmqnLkE")
    Call<CommonModelDataList> userFriendFacbook(@Path("socialId") String socialId);

    @FormUrlEncoded
    @POST(AppConstants.INSTAGRAM_ACCESS_TOKEN)
    Call<CommonModelDataObject> accessToken(@Field("client_id") String client_id,
                                            @Field("client_secret") String client_secret,
                                            @Field("grant_type") String grant_type,
                                            @Field("redirect_uri") String redirect_uri,
                                            @Field("code") String code);

    @GET(AppConstants.INSTAGRAM_BASIC_DISPLAY)
    Call<CommonModelDataObject> basicDisplay(@Path("socialId") String socialId,
                                             @Query("access_token") String token,
                                             @Query("fields") String Fields);

    @GET(AppConstants.INSTAGRAM_USER_DATA)
    Call<CommonModelDataObject> getProfileInfo(@Path("username") String username);

    @GET(AppConstants.INSTAGRAM_FOLLOWERS)
    Call<CommonModelDataObject> getFollwerList(@Query("access_token") String accessToken);

    @FormUrlEncoded
    @POST(AppConstants.SEARCH_USERS)
    Call<CommonModelDataList> searchUsers(@Field("search") String search,
                                            @Field("language") String langCode);
}






