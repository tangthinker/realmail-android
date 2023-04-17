package work.tangthinker.realmail.network.service

import com.google.gson.annotations.JsonAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import java.util.Date

interface PersonalInfoService {


    data class PersonalInfo(val uuid: String, val neckName: String, val sex: String, val personalSlogan: String,
                            val spaceBackground: String, val accountSignTime: String, val birthday: String, val headIcon: String,
                            val mailReceiveTime: String)

    data class PersonalInfoResult(val personal_info: PersonalInfo, val result: Int)

    data class UploadResult(val result: Int, val upload_files: Map<String, String>)

    data class CheckUserIdResult(val result: Int, val head_icon: String)


    @FormUrlEncoded
    @POST("/api/protected_resource/get_personal_info")
    fun getPersonalInfo(@Field("uuid") uuid: String, @Field("token") token: String) : Call<PersonalInfoResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/modify_all_personal_info")
    fun modifyAllPersonalInfo(@Body personalInfo: PersonalInfo) : Call<UserAuthService.SingleResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/modify_neck_name")
    fun modifyNeckName(@Field("uuid") uuid: String, @Field("token") token: String, @Field("neckName") neckName: String) : Call<UserAuthService.SingleResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/modify_personal_slogan")
    fun modifyPersonalSlogan(@Field("uuid") uuid: String, @Field("token") token: String, @Field("personalSlogan") personalSlogan: String) : Call<UserAuthService.SingleResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/modify_birthday")
    fun modifyBirthday(@Field("uuid") uuid: String, @Field("token") token: String, @Field("birthday") birthday: String) : Call<UserAuthService.SingleResult>


    @FormUrlEncoded
    @POST("/api/protected_resource/modify_mail_receive_time")
    fun modifyMailReceiveTime(@Field("uuid") uuid: String, @Field("token") token: String, @Field("mailReceiveTime") mailReceiveTime: String) : Call<UserAuthService.SingleResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/check_user_id_and_get_head_icon")
    fun checkUserIdAndGetHeadIcon(@Field("uuid") uuid: String, @Field("token") token: String, @Field("userId") userId: String): Call<CheckUserIdResult>

    @POST("/api/protected_resource/upload_and_modify_head_icon")
    fun modifyHeadIcon( @Body body: RequestBody) : Call<UploadResult>

    @POST("/api/protected_resource/upload_and_modify_space_background")
    fun modifySpaceBackground(@Body body: RequestBody) : Call<UploadResult>



}