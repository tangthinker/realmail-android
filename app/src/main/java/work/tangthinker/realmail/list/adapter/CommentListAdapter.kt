package work.tangthinker.realmail.list.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import work.tangthinker.realmail.R
import work.tangthinker.realmail.network.service.CommentService
import work.tangthinker.realmail.network.service.RealMailService

class CommentListAdapter (activity: Activity, private val resId: Int, data: List<CommentService.CommentOrReply>):
    ArrayAdapter<CommentService.CommentOrReply>(activity, resId, data){

    inner class ViewHolder(val send_user_id: TextView, val flag_reply: TextView, val receive_user_id: TextView, val comment_payload: TextView )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null){
            view = LayoutInflater.from(context).inflate(resId, parent, false)
            val sendUserId = view.findViewById<TextView>(R.id.tv_send_user_id)
            val flagReply = view.findViewById<TextView>(R.id.tv_flag_reply)
            val receiveUserId = view.findViewById<TextView>(R.id.tv_receive_user_id)
            val commentPayload = view.findViewById<TextView>(R.id.tv_comment_payload)
            viewHolder = ViewHolder(sendUserId, flagReply, receiveUserId, commentPayload)
            view.tag = viewHolder
        }else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val commentOrReply = getItem(position)

        commentOrReply?.let {
            viewHolder.send_user_id.text = commentOrReply.sendUserId
            if (commentOrReply.isReply == 1){
                viewHolder.flag_reply.visibility = View.VISIBLE
                viewHolder.receive_user_id.text = commentOrReply.repliedUserId
            }else{
                viewHolder.flag_reply.visibility = View.GONE
                viewHolder.receive_user_id.visibility = View.GONE
            }
            viewHolder.comment_payload.text = commentOrReply.payload
        }


        return view
    }


}

