package kumeda.cloudfirestore.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_comments.*
import kumeda.cloudfirestore.Adapters.CommentsAdapter
import kumeda.cloudfirestore.Model.Comment
import kumeda.cloudfirestore.R
import kumeda.cloudfirestore.Utillites.*

class CommentsActivity : AppCompatActivity() {

    lateinit var thoughtDocumentId: String
    lateinit var commentsAdapter : CommentsAdapter
    val comments = arrayListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        thoughtDocumentId = intent.getStringExtra(DOCUMENT_KEY)

        commentsAdapter = CommentsAdapter(comments)
        commentListView.adapter = commentsAdapter
        val layoutManager = LinearLayoutManager(this)
        commentListView.layoutManager = layoutManager

        FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)
            .collection(COMMENTS_REF)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                if(exception != null){
                    Log.e("Exception", "Could not retrieve comments ${exception.localizedMessage}")
                }

                if(snapshot != null) {
                    comments.clear()

                    for (document in snapshot.documents){
                        val data = document.data
                        val name = data?.get(USERNAME) as String
                        val timestamp = data[TIMESTAMP] as Timestamp
                        val commentTxt = data[COMMENT_TXT] as String

                        val newComment = Comment(name, timestamp.toDate(), commentTxt)
                        comments.add(newComment)
                    }

                    commentsAdapter.notifyDataSetChanged()
                }
            }
    }

    fun addCommentClicked(view: View) {

        val commentTxt = enterCommentTxt.text.toString()
        val thoughtRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)

        FirebaseFirestore.getInstance().runTransaction { transaction ->

            val thought = transaction.get(thoughtRef)
            val numComments = thought.getLong(NUM_COMMENTS)?.plus(1)
            transaction.update(thoughtRef, NUM_COMMENTS, numComments)

            val newCommentRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                .document(thoughtDocumentId).collection(COMMENTS_REF).document()

            val data = HashMap<String, Any>()
            data.put(COMMENT_TXT, commentTxt)
            data.put(TIMESTAMP, FieldValue.serverTimestamp())
            data.put(USERNAME, FirebaseAuth.getInstance().currentUser?.displayName.toString())

            transaction.set(newCommentRef, data)
        }
            .addOnSuccessListener {
                enterCommentTxt.setText("")
                //hideKeyboard()
            }
            .addOnFailureListener {exception ->
                Log.e("Exception", "Could not add comment $exception")

            }
    }
}
//private fun hideKeyboard() {
//    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    if (inputManager.isAcceptingText) {
//        inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
//    }
//}


