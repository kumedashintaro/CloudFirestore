package kumeda.cloudfirestore.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kumeda.cloudfirestore.Model.Comment
import kumeda.cloudfirestore.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentsAdapter(val comments: ArrayList<Comment>): RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.comment_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindComment(comments[position])
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        val username = itemView?.findViewById<TextView>(R.id.commentListUsername)
        val timestamp = itemView?.findViewById<TextView>(R.id.commentListTimestamp)
        val commentTxt = itemView?.findViewById<TextView>(R.id.commentListCommentListTxt)

        fun bindComment(comment: Comment) {

            username?.text = comment.username
            commentTxt?.text = comment.commentTxt


            val dateFormatter = SimpleDateFormat("MM d, h:mm a", Locale.getDefault())
            val dateString = dateFormatter.format(comment.timestamp)
            timestamp?.text = dateString

        }
    }
}