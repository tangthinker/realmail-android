package work.tangthinker.realmail.utils

import android.content.SharedPreferences

class SharedPreferencesHelper(private val preferences: SharedPreferences) {


    data class TokenPackage(val uuid: String?, val token: String?)

    fun getTokenPackage(): TokenPackage {
        return TokenPackage(preferences.getString("uuid", null), preferences.getString("token", null))
    }

    fun setTokenPackage(tokenPackage: TokenPackage){
        val editor = preferences.edit()
        editor.putString("uuid", tokenPackage.uuid)
        editor.putString("token", tokenPackage.token)
        editor.apply()
    }

    fun setUserId(userId: String){
        preferences.edit().putString("user_id", userId).apply()
    }

    fun getUserId(): String? {
        return preferences.getString("user_id", null)
    }

    fun setString(key: String, value: String){
        preferences.edit().putString(key, value).apply()
    }

    fun clear(){
        preferences.edit().clear().apply()
    }



}