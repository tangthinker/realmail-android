package work.tangthinker.realmail.list.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import work.tangthinker.realmail.R
import work.tangthinker.realmail.network.service.RealMailService

class MailCollectionAdapter(activity: Activity, private val resId: Int, data: List<RealMailService.MailCollectionWhitPersonalInfo>):
    ArrayAdapter<RealMailService.MailCollectionWhitPersonalInfo>(activity, resId, data) {

    inner class ViewHolder(val mail_time: TextView, val mail_payload: TextView, val userNeckName: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View
        val viewHolder: ViewHolder
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(resId, parent,false)
            val mailTime : TextView = view.findViewById(R.id.tv_mail_time)
            val mailPayload: TextView = view.findViewById(R.id.tv_mail_payload)
            val userNeckName: TextView = view.findViewById(R.id.tv_send_neck_name)
            viewHolder = ViewHolder(mailTime, mailPayload, userNeckName)
            view.tag = viewHolder
        }else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val mailWhitPersonalInfo = getItem(position)
        mailWhitPersonalInfo?.let {
            viewHolder.mail_time.text = mailWhitPersonalInfo.mailCollection.receiveTime
            viewHolder.mail_payload.text = mailWhitPersonalInfo.mailCollection.mailPayload
            viewHolder.userNeckName.text = mailWhitPersonalInfo.personalInfo.neckName
        }

        return view
    }









}