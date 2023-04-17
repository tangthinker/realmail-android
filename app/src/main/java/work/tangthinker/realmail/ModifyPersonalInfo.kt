package work.tangthinker.realmail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import com.yalantis.ucrop.UCrop
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.tangthinker.realmail.cache.CacheService
import work.tangthinker.realmail.cache.CacheServiceSharedPreferencesImp
import work.tangthinker.realmail.databinding.ActivityModifyPersonalInfoBinding
import work.tangthinker.realmail.network.service.PersonalInfoService
import work.tangthinker.realmail.network.service.UserAuthService
import work.tangthinker.realmail.utils.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ModifyPersonalInfo : AppCompatActivity() {

    private lateinit var binding: ActivityModifyPersonalInfoBinding

    private val personalInfoService = RetrofitServiceCreator.create<PersonalInfoService>()

    private lateinit var preferencesHelper: SharedPreferencesHelper

    private lateinit var inputDialog: InputDialog

    private var mailReceiveTime = Date()

    private var birthdayDate = Date()

    private val mailReceiveTimeFormat = SimpleDateFormat("每天HH:mm")

    private val birthdayFormat = SimpleDateFormat("yyyy年MM月dd日")

    private lateinit var tokenPackage : SharedPreferencesHelper.TokenPackage

    private lateinit var toastCreator : ToastCreator

    private lateinit var cacheService: CacheService

    private val CACHE_NAME = "personal_info"


    // 0 headIcon 1 spaceImage
    private var headIcon_or_spaceImage = 0

    private val pickImg = registerForActivityResult(ActivityResultContracts.OpenDocument()){
        println(it)
        val uri = Uri.fromFile(File(this.getExternalFilesDir(null), "${System.currentTimeMillis()}.jpg"))
        if (headIcon_or_spaceImage == 0){
            UCrop.of(it, uri)
                .withAspectRatio(1F, 1F)
                .start(this)
        }else {
            UCrop.of(it, uri)
                .withAspectRatio(16F, 9F)
                .start(this)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            val uri = data?.let { UCrop.getOutput(it) }
            uri!!
            val file = File(uri.path)
            val userId = preferencesHelper.getUserId()!!
            if (headIcon_or_spaceImage == 0){
                personalInfoService.modifyHeadIcon(FileUploadUtil.generateHeadIconUploadBody(userId, tokenPackage, file)).enqueue(object : Callback<PersonalInfoService.UploadResult> {
                        override fun onResponse(call: Call<PersonalInfoService.UploadResult>, response: Response<PersonalInfoService.UploadResult>) {
                            val uploadResult = response.body()
                            println(uploadResult)
                            uploadResult?.let {
                                if (uploadResult.result == 1) {
                                    toastCreator.modifySuccessful.show()
                                    var imagePath: String = ""
                                    for (headIcon in uploadResult.upload_files.keys) {
                                        imagePath =
                                            uploadResult.upload_files.get(headIcon).toString()
                                    }
                                    val imageUrl = "${RetrofitServiceCreator.BASE_URL}${imagePath}"
                                    Glide.with(this@ModifyPersonalInfo).load(imageUrl)
                                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                        .into(binding.ivHeadIcon)
                                    return
                                }
                            }
                            toastCreator.modifyFailed.show()
                        }

                        override fun onFailure(call: Call<PersonalInfoService.UploadResult>, t: Throwable) {
                            toastCreator.networkError.show()
                        }

                    }
                )
            }else {
                personalInfoService.modifySpaceBackground(FileUploadUtil.generateSpaceBackgroundUploadBody(userId, tokenPackage, file)).enqueue(object : Callback<PersonalInfoService.UploadResult>{
                    override fun onResponse(
                        call: Call<PersonalInfoService.UploadResult>,
                        response: Response<PersonalInfoService.UploadResult>
                    ) {
                        val uploadResult = response.body()
                        println(uploadResult)
                        uploadResult?.let {
                            if (uploadResult.result == 1) {
                                toastCreator.modifySuccessful.show()
                                var imagePath: String = ""
                                for (headIcon in uploadResult.upload_files.keys) {
                                    imagePath =
                                        uploadResult.upload_files[headIcon].toString()
                                }
                                val imageUrl = "${RetrofitServiceCreator.BASE_URL}${imagePath}"
//                                Glide.with(this@ModifyPersonalInfo).load(imageUrl)
//                                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
//                                    .into(binding.ivHeadIcon)
                                return
                            }
                        }
                        toastCreator.modifyFailed.show()
                    }

                    override fun onFailure(
                        call: Call<PersonalInfoService.UploadResult>,
                        t: Throwable
                    ) {

                    }

                })
            }

        }else if( resultCode == UCrop.RESULT_ERROR){
            val throwable = data?.let { UCrop.getError(it) }
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val loadingDialog = LoadingDialog(this, layoutInflater)
        loadingDialog.show()
        toastCreator = ToastCreator(this)

        cacheService = CacheServiceSharedPreferencesImp(this)

        inputDialog = InputDialog("输入信息",this, layoutInflater)

        preferencesHelper = SharedPreferencesHelper(getSharedPreferences("authInfo", MODE_PRIVATE))

        tokenPackage = preferencesHelper.getTokenPackage()

        if (cacheService.isCacheExists(CACHE_NAME)){
            setData(cacheService.getCache(CACHE_NAME, PersonalInfoService.PersonalInfoResult::class.java))
        }

        personalInfoService.getPersonalInfo(tokenPackage.uuid!!, tokenPackage.token!!).enqueue(object :
            Callback<PersonalInfoService.PersonalInfoResult> {
            override fun onResponse(
                call: Call<PersonalInfoService.PersonalInfoResult>,
                response: Response<PersonalInfoService.PersonalInfoResult>
            ) {
                println(response.body())
                val personalInfoResult = response.body()
                personalInfoResult?.let {
                    setData(personalInfoResult)
                    cacheService.saveCache(CACHE_NAME, personalInfoResult)
                    loadingDialog.dismiss()
                    return
                }
                loadingDialog.dismiss()
                toastCreator.networkError.show()


            }

            override fun onFailure(
                call: Call<PersonalInfoService.PersonalInfoResult>,
                t: Throwable
            ) {
                loadingDialog.dismiss()
                toastCreator.networkError.show()
                finish()
            }

        })


        binding.btnDonePersonalInfo.setOnClickListener {
            finish()
        }

        binding.rlHeadIcon.setOnClickListener {
//            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            headIcon_or_spaceImage = 0
            pickImg.launch(arrayOf("image/*"))
        }

        binding.rlSpaceImage.setOnClickListener {
            headIcon_or_spaceImage = 1
            pickImg.launch(arrayOf("image/*"))
        }


        binding.rlNeckName.setOnClickListener {
            inputDialog.show(object : InputDialog.OnConfirmClick{

                override fun onConfirmClick(inputData: String) {
                    println(inputData)
                    if ("" == inputData.trim()){
                        toastCreator.inputDataInvalid.show()
                        return
                    }
                    personalInfoService.modifyNeckName(tokenPackage.uuid!!, tokenPackage.token!!, inputData).enqueue(
                        object : Callback<UserAuthService.SingleResult>{
                            override fun onResponse(
                                call: Call<UserAuthService.SingleResult>,
                                response: Response<UserAuthService.SingleResult>
                            ) {
                                val singleResult = response.body()
                                singleResult?.let {
                                    if (singleResult.result == 1){
                                        toastCreator.modifySuccessful.show()
                                        binding.tvNeckName.text = inputData
                                        inputDialog.dismiss()
                                        return
                                    }
                                }
                                toastCreator.modifyFailed.show()
                                inputDialog.dismiss()
                            }

                            override fun onFailure(
                                call: Call<UserAuthService.SingleResult>,
                                t: Throwable
                            ) {
                                toastCreator.networkError.show()
                                inputDialog.dismiss()
                            }

                        }
                    )
                }
            })
            inputDialog.setTitle("修改邮名")
        }

        binding.rlPersonalSlogan.setOnClickListener {
            inputDialog.show(object : InputDialog.OnConfirmClick{
                override fun onConfirmClick(inputData: String) {
                    println(inputData)
                    if ("" == inputData.trim()){
                        toastCreator.inputDataInvalid.show()
                        return
                    }

                    personalInfoService.modifyPersonalSlogan(tokenPackage.uuid!!, tokenPackage.token!!, inputData).enqueue(
                        object : Callback<UserAuthService.SingleResult>{
                            override fun onResponse(
                                call: Call<UserAuthService.SingleResult>,
                                response: Response<UserAuthService.SingleResult>
                            ) {
                                val singleResult = response.body()
                                singleResult?.let {
                                    if (singleResult.result == 1){
                                        toastCreator.modifySuccessful.show()
                                        binding.tvSlogan.text = inputData
                                        inputDialog.dismiss()
                                        return
                                    }
                                }
                                toastCreator.modifyFailed.show()
                                inputDialog.dismiss()
                            }

                            override fun onFailure(
                                call: Call<UserAuthService.SingleResult>,
                                t: Throwable
                            ) {
                                toastCreator.networkError.show()
                                inputDialog.dismiss()
                            }

                        }
                    )

                }
            })
            inputDialog.setTitle("修改个性签名")
        }

        binding.rlReceiveTime.setOnClickListener {
            showMailReceiveTimeSettingDialog(tokenPackage, toastCreator)
        }

        binding.rlBirthday.setOnClickListener {
            showBirthdaySettingDialog(tokenPackage, toastCreator)
        }



    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.setting_close)
    }

    private fun setData(personalInfoResult: PersonalInfoService.PersonalInfoResult){
        binding.tvNeckName.text = personalInfoResult.personal_info.neckName
        binding.tvSlogan.text = personalInfoResult.personal_info.personalSlogan
        binding.tvBirthdayDate.text = personalInfoResult.personal_info.birthday
        binding.tvReceiveTime.text = personalInfoResult.personal_info.mailReceiveTime
        birthdayDate = birthdayFormat.parse(personalInfoResult.personal_info.birthday) as Date
        mailReceiveTime = mailReceiveTimeFormat.parse(personalInfoResult.personal_info.mailReceiveTime) as Date
        if ("defaultHeadIcon" != personalInfoResult.personal_info.headIcon){
            val imageUrl = "${RetrofitServiceCreator.BASE_URL}${personalInfoResult.personal_info.headIcon}"
            Glide.with(this@ModifyPersonalInfo).load(imageUrl).apply(
                RequestOptions.bitmapTransform(
                    CircleCrop()
                )
            ).into(binding.ivHeadIcon)
        }
    }


    private fun showMailReceiveTimeSettingDialog(
        tokenPackage: SharedPreferencesHelper.TokenPackage,
        toastCreator: ToastCreator
    ) {
        CardDatePickerDialog.builder(this)
            .showBackNow(false)
            .showFocusDateInfo(false)
            .showDateLabel(false)
            .setBackGroundModel(CardDatePickerDialog.STACK)
            .setPickerLayout(R.layout.layout_date_picker_mail_receive_time)
            .setTitle("选择信件接收时间")
            .setDefaultTime(mailReceiveTime.time)
            .setOnChoose {millisecond->
                println(millisecond)
                mailReceiveTime  = Date(millisecond)
                val formatDate = mailReceiveTimeFormat.format(mailReceiveTime)
                println(formatDate)

                personalInfoService.modifyMailReceiveTime(tokenPackage.uuid!!, tokenPackage.token!!, formatDate).enqueue(object : Callback<UserAuthService.SingleResult>{
                    override fun onResponse(
                        call: Call<UserAuthService.SingleResult>,
                        response: Response<UserAuthService.SingleResult>
                    ) {
                        val singleResult = response.body()
                        singleResult?.let {
                            if (singleResult.result == 1){
                                toastCreator.modifySuccessful.show()
                                binding.tvReceiveTime.text = formatDate
                                return
                            }
                        }
                        toastCreator.modifyFailed.show()
                    }

                    override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                        toastCreator.networkError.show()
                    }

                })
            }.build().show()
    }

    private fun showBirthdaySettingDialog(
        tokenPackage: SharedPreferencesHelper.TokenPackage,
        toastCreator: ToastCreator
    ) {
        CardDatePickerDialog.builder(this)
            .showBackNow(false)
            .showFocusDateInfo(false)
            .showDateLabel(false)
            .setBackGroundModel(CardDatePickerDialog.STACK)
            .setPickerLayout(R.layout.layout_date_picker_birthday)
            .setTitle("修改生日")
            .setDefaultTime(birthdayDate.time)
            .setOnChoose {millisecond->
                println(millisecond)
                birthdayDate  = Date(millisecond)
                val formatDate = birthdayFormat.format(birthdayDate)
                println(formatDate)
                personalInfoService.modifyBirthday(tokenPackage.uuid!!, tokenPackage.token!!, formatDate).enqueue(object : Callback<UserAuthService.SingleResult>{
                    override fun onResponse(
                        call: Call<UserAuthService.SingleResult>,
                        response: Response<UserAuthService.SingleResult>
                    ) {
                        val singleResult = response.body()
                        singleResult?.let {
                            if (singleResult.result == 1){
                                toastCreator.modifySuccessful.show()
                                binding.tvBirthdayDate.text = formatDate
                                return
                            }
                        }
                        toastCreator.modifyFailed.show()
                    }

                    override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                        toastCreator.networkError.show()
                    }

                })
            }.build().show()
    }



}