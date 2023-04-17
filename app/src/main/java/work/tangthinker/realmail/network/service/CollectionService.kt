package work.tangthinker.realmail.network.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CollectionService {


    @FormUrlEncoded
    @POST("/api/protected_resource/collection/addCollection")
    fun addCollection(@Field("uuid") uuid: String, @Field("token") token: String, @Field("mail_id") mail_id: String) : Call<UserAuthService.SingleResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/collection/removeCollection")
    fun removeCollection(@Field("uuid") uuid: String, @Field("token") token: String, @Field("collectionId") collectionId: String) : Call<UserAuthService.SingleResult>

}

