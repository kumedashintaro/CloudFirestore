package kumeda.cloudfirestore.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kumeda.cloudfirestore.Model.Thought
import kumeda.cloudfirestore.R
import kumeda.cloudfirestore.Utillites.NUM_LIKES
import kumeda.cloudfirestore.Utillites.THOUGHTS_REF
import kumeda.cloudfirestore.`interface`.ThoughtOptinsClickListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ThoughtsAdapter(val thoughts: ArrayList<Thought>, val thoughtOptionsListener: ThoughtOptinsClickListener, val itemClick: (Thought) -> Unit) :
    RecyclerView.Adapter<ThoughtsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.thought_list_view, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return thoughts.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindThought(thoughts[position])
    }

    inner class ViewHolder(itemView: View?, val itemClick: (Thought) -> Unit) : RecyclerView.ViewHolder(itemView!!) {

        val username = itemView?.findViewById<TextView>(R.id.listViewUsername)
        val timestamp = itemView?.findViewById<TextView>(R.id.listViewTimestamp)
        val thoughtTxt = itemView?.findViewById<TextView>(R.id.listViewThoughtTxt)
        val numLikes = itemView?.findViewById<TextView>(R.id.listViewNumLikesLb)
        val likesImages = itemView?.findViewById<ImageView>(R.id.listViewLikesImage)
        val numComments = itemView?.findViewById<TextView>(R.id.numCommentsLbl)
        val optionsImage = itemView?.findViewById<ImageView>(R.id.thoughtOptionsImage)

        fun bindThought(thought: Thought) {

            optionsImage?.visibility = View.INVISIBLE
            username?.text = thought.username
            thoughtTxt?.text = thought.thoughtTxt
            numLikes?.text = thought.numLikes.toString()
            itemView.setOnClickListener { itemClick(thought) }
            numComments?.text = thought.NumComments.toString()

            val dateFormatter = SimpleDateFormat("MM d, h:mm a", Locale.getDefault())
            val dateString = dateFormatter.format(thought.timestamp)
            timestamp?.text = dateString

            likesImages?.setOnClickListener{
                FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thought.documentId)
                    .update(NUM_LIKES, thought.numLikes +1)
            }
            if(FirebaseAuth.getInstance().currentUser?.uid == thought.userId){
                optionsImage?.visibility = View.VISIBLE
                optionsImage?.setOnClickListener {
                   thoughtOptionsListener.thoughtOptionsMenunClicked(thought)

                }
            }
        }
    }
}