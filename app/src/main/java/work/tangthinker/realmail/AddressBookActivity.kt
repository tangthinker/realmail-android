package work.tangthinker.realmail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.tangthinker.realmail.cache.CacheService
import work.tangthinker.realmail.cache.CacheServiceSharedPreferencesImp
import work.tangthinker.realmail.databinding.ActivityAddressBookBinding
import work.tangthinker.realmail.list.adapter.AddressBookAdapter
import work.tangthinker.realmail.network.service.AddressBookService
import work.tangthinker.realmail.network.service.UserAuthService
import work.tangthinker.realmail.utils.*

class AddressBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBookBinding
    
    private val addressBookService = RetrofitServiceCreator.create<AddressBookService>()

    private lateinit var preferencesHelper: SharedPreferencesHelper

    private lateinit var tokenPackage: SharedPreferencesHelper.TokenPackage

    private lateinit var toastCreator: ToastCreator

    private lateinit var inputDialog: InputDialog

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var cacheService: CacheService

    private val CACHE_NAME = "address_book"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = SharedPreferencesHelper(getSharedPreferences("authInfo", MODE_PRIVATE))

        tokenPackage = preferencesHelper.getTokenPackage()

        cacheService = CacheServiceSharedPreferencesImp(this)

        toastCreator = ToastCreator(this)

        inputDialog = InputDialog("通过RealMail ID添加邮友", this, layoutInflater)

        loadingDialog = LoadingDialog(this, layoutInflater)

        binding.btnAddAddressBook.setOnClickListener {
            inputDialog.show(object : InputDialog.OnConfirmClick{
                override fun onConfirmClick(inputData: String) {
                    loadingDialog.show()
                    addressBookService.addAddressUsingUserId(tokenPackage.uuid!!, tokenPackage.token!!, inputData).enqueue(object : Callback<UserAuthService.SingleResult>{
                        override fun onResponse(call: Call<UserAuthService.SingleResult>, response: Response<UserAuthService.SingleResult>) {
                            val singleResult = response.body()
                            singleResult?.let {
                                if (singleResult.result == 1){
                                    toastCreator.addSuccessful.show()
                                    loadingDialog.dismiss()
                                    getData()
                                    return
                                }
                            }
                            toastCreator.addFailed.show()
                            loadingDialog.dismiss()
                        }

                        override fun onFailure(call: Call<UserAuthService.SingleResult>, t: Throwable) {
                            toastCreator.networkError.show()
                            loadingDialog.dismiss()
                        }

                    })
                    inputDialog.dismiss()
                }
            })
        }

        if (cacheService.isCacheExists(CACHE_NAME)){
            setData(cacheService.getCache(CACHE_NAME, AddressBookService.GetAddressBookResult::class.java))
        }

        getData()


    }


    private fun getData(){
        addressBookService.getAddressBook(tokenPackage.uuid!!, tokenPackage.token!!).enqueue(object :Callback<AddressBookService.GetAddressBookResult>{
            override fun onResponse(call: Call<AddressBookService.GetAddressBookResult>, response: Response<AddressBookService.GetAddressBookResult>) {
                val getAddressBookResult = response.body()
                getAddressBookResult?.let {
                    println(getAddressBookResult)
                    if (getAddressBookResult.result == 1){
                        if (getAddressBookResult.personal_info.isNotEmpty()){
                            setData(getAddressBookResult)
                            cacheService.saveCache(CACHE_NAME, getAddressBookResult)
                        }else {
                            binding.llAddressBookEmptyTip.visibility = View.VISIBLE
                            binding.lvAddress.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AddressBookService.GetAddressBookResult>, t: Throwable) {
                toastCreator.networkError.show()
            }

        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.horizontal_close)
    }

    override fun onRestart() {
        super.onRestart()
        getData()
    }

    private fun setData(getAddressBookResult: AddressBookService.GetAddressBookResult){
        binding.lvAddress.adapter = AddressBookAdapter(this@AddressBookActivity, R.layout.layout_address_item, getAddressBookResult.personal_info)
        binding.llAddressBookEmptyTip.visibility = View.GONE
        binding.lvAddress.visibility = View.VISIBLE
        binding.lvAddress.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this@AddressBookActivity, OtherPersonalSpaceActivity::class.java)
            intent.putExtra("enterType", 1)
            intent.putExtra("personalInfo", getAddressBookResult.personal_info[position])
            intent.putExtra("userId", getAddressBookResult.address_book[position].userId)
            intent.putExtra("addressId", getAddressBookResult.address_book[position].addressId)
            startActivity(intent)
            overridePendingTransition(R.anim.horizontal_open, R.anim.bottom_silent)
        }
    }


}