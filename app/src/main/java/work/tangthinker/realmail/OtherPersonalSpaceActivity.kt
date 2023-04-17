package work.tangthinker.realmail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Response
import work.tangthinker.realmail.databinding.ActivityPersonalSpaceBinding
import work.tangthinker.realmail.network.service.AddressBookService
import work.tangthinker.realmail.network.service.RealMailService
import work.tangthinker.realmail.network.service.UserAuthService
import work.tangthinker.realmail.utils.LoadingDialog
import work.tangthinker.realmail.utils.RetrofitServiceCreator
import work.tangthinker.realmail.utils.SharedPreferencesHelper
import work.tangthinker.realmail.utils.ToastCreator
import javax.security.auth.callback.Callback

class OtherPersonalSpaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalSpaceBinding

    private lateinit var preferences : SharedPreferences

    private lateinit var loadingDialog: LoadingDialog

    private val addressBookService = RetrofitServiceCreator.create<AddressBookService>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPersonalSpaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this, layoutInflater)

        preferences = getSharedPreferences("authInfo", MODE_PRIVATE)
        val sharedPreferencesHelper = SharedPreferencesHelper(preferences)
        val loadingDialog = LoadingDialog(this, layoutInflater)
        val toastCreator = ToastCreator(this)

        val tokenPackage = sharedPreferencesHelper.getTokenPackage()

        var personalInfo : RealMailService.PersonalInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("personalInfo", RealMailService.PersonalInfo::class.java)!!
        }else{
            intent.getSerializableExtra("personalInfo") as RealMailService.PersonalInfo
        }

        val enterType = intent.getIntExtra("enterType", 0)

        val userId = intent.getStringExtra("userId")

        binding.tvTitle.text = "${personalInfo.neckName}的个人空间"
        binding.tvNeckName.text = personalInfo.neckName
        binding.tvSlogan.text = personalInfo.personalSlogan
        binding.tvBirthdayDate.text = personalInfo.birthday
        binding.tvReceiveTime.text = personalInfo.mailReceiveTime
        binding.tvSignTime.text = personalInfo.accountSignTime
        binding.tvUserId.text = userId
        if ("defaultHeadIcon" != personalInfo.headIcon){
            val imageUrl = "${RetrofitServiceCreator.BASE_URL}${personalInfo.headIcon}"
            Glide.with(this@OtherPersonalSpaceActivity).load(imageUrl).apply(
                RequestOptions.bitmapTransform(
                    RoundedCorners(20)
                )).into(binding.ivHeadIcon)
        }

        if ("defaultBackground" != personalInfo.spaceBackground){
            val imageUrl = "${RetrofitServiceCreator.BASE_URL}${personalInfo.spaceBackground}"
            Glide.with(this@OtherPersonalSpaceActivity).load(imageUrl)
                .into(binding.ivSpaceImage)
        }

        if (enterType == 0){

            binding.rlSendMailTo.visibility = View.GONE

            binding.rlAddToAddressBook.setOnClickListener {
                loadingDialog.show()
                addressBookService.addAddressToBook(tokenPackage.uuid!!, tokenPackage.token!!, personalInfo.uuid)
                    .enqueue(object : retrofit2.Callback<UserAuthService.SingleResult>{
                        override fun onResponse(call: Call<UserAuthService.SingleResult>, response: Response<UserAuthService.SingleResult>
                        ) {
                            val singleResult = response.body()
                            singleResult?.let {
                                if (singleResult.result == 1){
                                    toastCreator.addSuccessful.show()
                                    loadingDialog.dismiss()
                                    return
                                }
                            }
                            toastCreator.bookAlreadyExists.show()
                            loadingDialog.dismiss()
                        }

                        override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                            toastCreator.networkError.show()
                            loadingDialog.dismiss()
                        }

                    })
            }
        } else {
            binding.rlAddToAddressBook.setBackgroundResource(R.drawable.btn_red)
            binding.tvAddOrDelete.text = "删除该联系人"


            binding.rlSendMailTo.setOnClickListener {
                val intent = Intent(this@OtherPersonalSpaceActivity, NewRealMailActivity::class.java)
                intent.putExtra("reply_user_id", userId)
                startActivity(intent)
                overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
            }

            binding.rlAddToAddressBook.setOnClickListener {
                //删除联系人
                loadingDialog.show()
                addressBookService.removeAddressItem(tokenPackage.uuid!!, tokenPackage.token!!, intent.getStringExtra("addressId")!!)
                    .enqueue(object : retrofit2.Callback<UserAuthService.SingleResult>{
                        override fun onResponse(
                            call: Call<UserAuthService.SingleResult>,
                            response: Response<UserAuthService.SingleResult>
                        ) {
                            val singleResult = response.body()
                            singleResult?.let {
                                if (singleResult.result == 1){
                                    toastCreator.removeSuccessful.show()
                                    loadingDialog.dismiss()
                                    finish()
                                    return
                                }
                            }
                            toastCreator.removeFailed.show()
                            loadingDialog.dismiss()
                        }

                        override fun onFailure(
                            call: Call<UserAuthService.SingleResult>,
                            t: Throwable
                        ) {
                            toastCreator.networkError.show()
                            loadingDialog.dismiss()
                        }

                    })
            }

        }






    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.horizontal_close)
    }




}