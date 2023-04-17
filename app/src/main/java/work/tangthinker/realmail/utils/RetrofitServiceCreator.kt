package work.tangthinker.realmail.utils

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitServiceCreator {

//    const val BASE_URL = "http://192.168.1.105:1465"

//    const val BASE_URL = "http://114.132.156.177:1465"

    const val BASE_URL = "http://realmail.tangthinker.work"

//    const val BASE_URL = "http://192.168.43.20:1465"

//      const val BASE_URL = "http://192.168.1.160:1465"


//        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy年MM月dd日 HH:mm").create()))

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>) : T = retrofit.create(serviceClass)

    inline fun <reified T> create() : T = create(T::class.java)

}