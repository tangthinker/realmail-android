package work.tangthinker.realmail.network.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CommentService {


    data class Comment(val commentId: String, val attachId: String, val isReply: Int, val payload: String,
                        val sendUuid: String, val sendTime: String, val sendUserId: String)

    data class CommentWithReplies(val comment: Comment, val replies: List<Comment>)

    data class GetCommentsWithRepliesResult(val comments_with_replies: List<CommentWithReplies>, val result_time_stamp: Long,
                                            val response_size: Int, val result: Int)

    data class CommentOrReply(val commentId: String, val attachId: String, val isReply: Int, val payload: String,
                    val sendUuid: String, val sendTime: String, val sendUserId: String, val repliedUserId: String?){
        constructor(comment: Comment) : this(comment.commentId, comment.attachId, comment.isReply, comment.payload, comment.sendUuid, comment.sendTime,
        comment.sendUserId, null)

        constructor(comment: Comment, repliedUserId: String) : this(comment.commentId, comment.attachId, comment.isReply, comment.payload, comment.sendUuid, comment.sendTime,
            comment.sendUserId, repliedUserId)
    }

    @FormUrlEncoded
    @POST("/api/protected_resource/comment/getCommentsWithReplies")
    fun getCommentsWithReplies(@Field("uuid") uuid: String, @Field("token") token: String, @Field("mailId") mailId: String) : Call<GetCommentsWithRepliesResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/comment/comment")
    fun addComment(@Field("uuid") uuid: String, @Field("token") token: String, @Field("mailId") mailId: String, @Field("payload") payload: String) : Call<UserAuthService.SingleResult>

    @FormUrlEncoded
    @POST("/api/protected_resource/comment/reply")
    fun addReply(@Field("uuid") uuid: String, @Field("token") token: String, @Field("commentId") commentId: String, @Field("payload") payload: String) : Call<UserAuthService.SingleResult>


}