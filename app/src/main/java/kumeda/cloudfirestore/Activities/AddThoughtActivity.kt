package kumeda.cloudfirestore.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_thought.*
import kumeda.cloudfirestore.*
import kumeda.cloudfirestore.Utillites.*

class AddThoughtActivity : AppCompatActivity() {

    var selectedCategory = FUNNY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_thought)
    }

    fun addPostClicked(view: View) {
        // add post to Firestore!

        val data = HashMap<String, Any>()
        data.put(CATEGORY, selectedCategory)
        data.put(NUM_COMMENTS, 0)
        data.put(NUM_LIKES, 0)
        data.put(THOUGHT_TXT, addThoughtTxt.text.toString())
        data.put(TIMESTAMP, FieldValue.serverTimestamp())
        data.put(USERNAME, FirebaseAuth.getInstance().currentUser?.displayName.toString())


        FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
            .add(data)
            .addOnSuccessListener{
                finish()
            }
            .addOnFailureListener{exception ->
                Log.e("Exception","Could not add post: $exception")
            }

    }

    fun addFunnyClicked(view: View) {
        if (selectedCategory == FUNNY) {
            addFunnyBtn.isChecked = true
            return
        }
        addSeriousBtn.isChecked = false
        addCrazyBtn.isChecked = false
        selectedCategory = FUNNY
    }

    fun addSeriousClicked(view: View) {
        if (selectedCategory == SERIOUS) {
            addFunnyBtn.isChecked = true
            return
        }
        addFunnyBtn.isChecked = false
        addCrazyBtn.isChecked = false
        selectedCategory = SERIOUS

    }

    fun addCrazyClicked(view: View) {
        if (selectedCategory == CRAZY) {
            addFunnyBtn.isChecked = true
            return
        }
        addFunnyBtn.isChecked = false
        addSeriousBtn.isChecked = false
        selectedCategory = CRAZY
    }
}
