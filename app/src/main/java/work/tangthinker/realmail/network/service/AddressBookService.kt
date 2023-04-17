package work.tangthinker.realmail.network.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AddressBookService {


    data class AddressBook(val addressId: String, val addressUuid: String, val ownerUuid: String, val userId: String)

    data class GetAddressBookResult(val result: Int, val result_time_stamp: Long,
                                    val personal_info: List<RealMailService.PersonalInfo>, val address_book: List<AddressBook>)


    @FormUrlEncoded
    @POST("/api/protected_resource/address_book/getAddressBook")
    fun getAddressBook(@Field("uuid") uuid: String, @Field("token") token: String) : Call<GetAddressBookResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/address_book/addAddressToBook")
    fun addAddressToBook(@Field("uuid") uuid: String, @Field("token") token: String, @Field("addressUuid") addressUuid: String) : Call<UserAuthService.SingleResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/address_book/removeAddressItem")
    fun removeAddressItem(@Field("uuid") uuid: String, @Field("token") token: String, @Field("addressId") addressId: String) : Call<UserAuthService.SingleResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/address_book/addAddressUsingUserId")
    fun addAddressUsingUserId(@Field("uuid") uuid: String, @Field("token") token: String, @Field("addressUserId") addressUserId: String) : Call<UserAuthService.SingleResult>
}