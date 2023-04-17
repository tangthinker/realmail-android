package work.tangthinker.realmail.network.service


import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.Serializable

interface RealMailService {


    data class RealMail(val mailId: String, val publicId: String, val sendUuid: String, val receiveUuid: String,
                        val accessFlag: Int, val mailPayload: String, val sendTime: String, val receiveTime: String, val wasReceived: Int,
                        val sendUserId: String, val receiveUserId: String) : Serializable

    data class MailCollection(val collectionId: String, val mailId: String, val publicId: String, val sendUuid: String, val receiveUuid: String,
                        val accessFlag: Int, val mailPayload: String, val sendTime: String, val receiveTime: String, val wasReceived: Int,
                        val ownerUuid: String, val sendUserId: String, val receiveUserId: String) : Serializable

    data class PersonalInfo(val uuid: String, val neckName: String, val sex: String, val personalSlogan: String, val spaceBackground: String, val accountSignTime: String,
                            val birthday: String, val headIcon: String, val mailReceiveTime: String) : Serializable

    data class MailWhitPersonalInfo(val realMail: RealMail, val personalInfo: PersonalInfo) : Serializable

    data class MailCollectionWhitPersonalInfo(val mailCollection: MailCollection, val personalInfo: PersonalInfo) : Serializable


    data class MailServiceResult(val result: Int, val mail_public_id: String, val mail_id: String)

    data class GetPublicMailsResult(val public_mail: List<RealMail>, val personal_info: List<PersonalInfo>, val result: Int, val response_size: Int,
                                    val result_time_stamp: Long)

    data class GetPersonalMailsResult(val personal_mail: List<RealMail>, val personal_info: List<PersonalInfo>, val result: Int, val response_size: Int,
                                      val result_time_stamp: Long)

    data class GetMailCollectionsResult(val mail_collections: List<MailCollection>, val personal_info: List<PersonalInfo>, val result: Int, val response_size: Int,
                                        val result_time_stamp: Long)


    @FormUrlEncoded
    @POST("/api/protected_resource/mail/sendMail")
    fun sendMail(@Field("uuid") uuid: String, @Field("token") token: String,
    @Field("receiveUserId") receiveUserId: String?, @Field("accessFlag")accessFlag: Int,
    @Field("mailPayload")mailPayload: String, @Field("receiveTime")receiveTime: String) : Call<MailServiceResult>



    @FormUrlEncoded
    @POST("/api/protected_resource/mail/getPublicMail")
    fun getPublicMail(@Field("uuid") uuid: String, @Field("token") token: String) : Call<GetPublicMailsResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/mail/getPersonalMail")
    fun getPersonalMail(@Field("uuid") uuid: String, @Field("token") token: String) : Call<GetPersonalMailsResult>


    @FormUrlEncoded
    @POST("/api/protected_resource/mail/setWasReceived")
    fun setWasReceived(@Field("uuid") uuid: String, @Field("token") token: String, @Field("mail_id") mailId: String) : Call<UserAuthService.SingleResult>


    @FormUrlEncoded
    @POST("/api/protected_resource/collection/getAllCollections")
    fun getAllCollections(@Field("uuid") uuid: String, @Field("token") token: String) : Call<GetMailCollectionsResult>



}