package work.tangthinker.realmail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.tangthinker.realmail.databinding.ActivityLoginBinding
import work.tangthinker.realmail.network.service.UserAuthService
import work.tangthinker.realmail.utils.LoadingDialog
import work.tangthinker.realmail.utils.RetrofitServiceCreator
import work.tangthinker.realmail.utils.SharedPreferencesHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val userAuthService = RetrofitServiceCreator.create<UserAuthService>()

    private lateinit var preferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loadingDialog = LoadingDialog(this, layoutInflater)
        preferences = getSharedPreferences("authInfo", MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(preferences)


        binding.rlLogin.setOnClickListener {

            val userId = binding.etUserId.text
            val password = binding.etPassword.text

            println("username: $userId password: $password")
            val intent = Intent(this, MainActivity::class.java)
            val loginErrorTips = Toast.makeText(this, "账号或密码错误！", Toast.LENGTH_SHORT)
            val networkErrorTips = Toast.makeText(this, "网络连接失败！", Toast.LENGTH_SHORT)

            loadingDialog.show()

            userAuthService.login(userId.toString(), password.toString()).enqueue(object : Callback<UserAuthService.UserAuthResult> {
                override fun onResponse(
                    call: Call<UserAuthService.UserAuthResult>,
                    response: Response<UserAuthService.UserAuthResult>
                ) {
                    loadingDialog.dismiss()
                    val userAuthResult = response.body()
                    if (userAuthResult != null) {
                        if (userAuthResult.result == 1){
                            sharedPreferencesHelper.setTokenPackage(SharedPreferencesHelper.TokenPackage(userAuthResult.uuid, userAuthResult.token))
                            sharedPreferencesHelper.setUserId(userAuthResult.user_id)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            return
                        }
                    }
                    loginErrorTips.show()
                }

                override fun onFailure(call: Call<UserAuthService.UserAuthResult>, t: Throwable) {
                    loadingDialog.dismiss()
                    networkErrorTips.show()
                }
            })

        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.setting_open, R.anim.bottom_silent)
        }

        binding.tvLostPwd.setOnClickListener {
            val intent = Intent(this, ModifyPasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.setting_open, R.anim.bottom_silent)
        }




    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.setting_close)

    }




}