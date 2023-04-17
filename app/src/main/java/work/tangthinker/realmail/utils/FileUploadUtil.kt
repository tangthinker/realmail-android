package work.tangthinker.realmail.utils

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object FileUploadUtil {


    fun generateHeadIconUploadBody(userId: String, tokenPackage: SharedPreferencesHelper.TokenPackage, file: File): MultipartBody {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("uuid", tokenPackage.uuid!!)
            .addFormDataPart("token", tokenPackage.token!!)
            .addFormDataPart("user_id", userId)
            .addFormDataPart(
                "headIcon",
                file.name,
                RequestBody.create(MediaType.parse("image/*"), file)
            )
            .build()
        return body
    }

    fun generateSpaceBackgroundUploadBody(userId: String, tokenPackage: SharedPreferencesHelper.TokenPackage, file: File): MultipartBody {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("uuid", tokenPackage.uuid!!)
            .addFormDataPart("token", tokenPackage.token!!)
            .addFormDataPart("user_id", userId)
            .addFormDataPart(
                "spaceBackground",
                file.name,
                RequestBody.create(MediaType.parse("image/*"), file)
            )
            .build()
        return body
    }


}