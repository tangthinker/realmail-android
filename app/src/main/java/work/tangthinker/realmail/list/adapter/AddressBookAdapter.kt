package work.tangthinker.realmail.list.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import work.tangthinker.realmail.R
import work.tangthinker.realmail.network.service.PersonalInfoService
import work.tangthinker.realmail.network.service.RealMailService
import work.tangthinker.realmail.utils.RetrofitServiceCreator

class AddressBookAdapter(activity: Activity, private val resId: Int, data: List<RealMailService.PersonalInfo>) :
    ArrayAdapter<RealMailService.PersonalInfo>(activity, resId, data){

    inner class ViewHolder(val headIcon: ImageView, val neckName: TextView)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: AddressBookAdapter.ViewHolder
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(resId, parent,false)
            val headIcon = view.findViewById<ImageView>(R.id.iv_head_icon)
            val neckName = view.findViewById<TextView>(R.id.tv_neck_name)
            viewHolder = ViewHolder(headIcon, neckName)
            view.tag = viewHolder
        }else {
            view = convertView
            viewHolder = view.tag as AddressBookAdapter.ViewHolder
        }

        val personalInfo = getItem(position)
        personalInfo?.let {
            viewHolder.neckName.text = personalInfo.neckName
            if ("defaultHeadIcon" != personalInfo.headIcon){
                val imageUrl = "${RetrofitServiceCreator.BASE_URL}${personalInfo.headIcon}"
                Glide.with(context).load(imageUrl).apply(
                    RequestOptions.bitmapTransform(
                        RoundedCorners(15)
                    )).into(viewHolder.headIcon)
            }
        }

        return view
    }





    }