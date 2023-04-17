package work.tangthinker.realmail.cache

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class CacheServiceSharedPreferencesImp(private val context: Context) : CacheService {


    private val preferences : SharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE)

    private val gson = GsonBuilder().create()


    override fun <T> saveCache(cacheName: String, payload: T) {
        val payloadJson = gson.toJson(payload)
        saveString(cacheName, payloadJson)
    }

     override fun <T> getCache(cacheName: String, cacheClass: Class<T>): T {
        val cacheString = this.getString(cacheName)
        return gson.fromJson(cacheString, cacheClass)
    }

    override fun isCacheExists(cacheName: String): Boolean {
        return getString(cacheName) != null
    }

    override fun clearCache(cacheName: String) {
        clearString(cacheName)
    }


    private fun saveString(key: String, value: String){
        preferences.edit().putString(key, value).apply()
    }

    private fun getString(key: String) : String? {
        return preferences.getString(key, null)
    }

    private fun clearString(key: String){
        preferences.edit().remove(key).apply()
    }
}