package work.tangthinker.realmail.network.service

import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAuthService {


    data class UserAuthResult(val result : Int, val user_id: String, val uuid: String, val token: String)

    data class SingleResult(val result: Int)

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("user_id") userId : String, @Field("password") password : String) : Call<UserAuthResult>

    @FormUrlEncoded
    @POST("logout")
    fun logout(@Field("uuid") uuid: String, @Field("token") token: String) : Call<SingleResult>

    @FormUrlEncoded
    @POST("checkUserId")
    fun checkUserId(@Field("user_id") userId: String) : Call<SingleResult>

    @FormUrlEncoded
    @POST("sendCode")
    fun sendCode(@Field("user_id") userId: String, @Field("email") email: String) : Call<SingleResult>

    @FormUrlEncoded
    @POST("register")
    fun register(@Field("user_id") userId: String, @Field("password") password: String, @Field("email") email: String,
                 @Field("verifyCode") verifyCode : String) : Call<UserAuthResult>

    @FormUrlEncoded
    @POST("modifyPassword")
    fun modifyPassword(@Field("user_id") userId: String, @Field("newPassword") newPassword: String, @Field("email") email: String,
                 @Field("verifyCode") verifyCode : String) : Call<UserAuthResult>

}