package kumeda.cloudfirestore.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_update_comment.*
import kumeda.cloudfirestore.R
import kumeda.cloudfirestore.Utillites.*

class UpdateCommentActivity : AppCompatActivity() {

    lateinit var thoughtDocId: String
    lateinit var commentDocId: String
    lateinit var commentTxt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_comment)

        thoughtDocId = intent.getStringExtra(THOUGHT_DOC_ID_EXTRA)
        commentDocId = intent.getStringExtra(COMMENT_DOC_ID_EXTRA)
        commentTxt = intent.getStringExtra(COMMENT_TXT_EXTRA)

        updateCommentTxt.setText(commentTxt)

    }

    fun updateCommentClicked(view: View) {
        FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocId)
            .collection(COMMENTS_REF).document(commentDocId)
            .update(COMMENT_TXT, updateCommentTxt.text.toString())
            .addOnSuccessListener{
                finish()
            }
            .addOnFailureListener {exception ->
                Log.e("Exception", "could not update comment: ${exception.localizedMessage}")
            }

    }

}
