package kumeda.cloudfirestore.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_user.*
import kumeda.cloudfirestore.Utillites.DATE_CREATED
import kumeda.cloudfirestore.R
import kumeda.cloudfirestore.Utillites.USERNAME
import kumeda.cloudfirestore.Utillites.USER_REF

class CreateUserActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        auth = FirebaseAuth.getInstance()
    }

    fun createCreateClicked(view: View){
        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()
        val username = createUsernameTxt.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {result ->

                val changeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()

                result.user.updateProfile(changeRequest)
                    .addOnFailureListener {exception ->
                        Log.e("Exception:", "Could not update display name: ${exception.localizedMessage} ")
                    }

                val data = HashMap<String, Any>()
                data.put(USERNAME, username)
                data.put(DATE_CREATED, FieldValue.serverTimestamp())

                FirebaseFirestore.getInstance().collection(USER_REF).document(result.user.uid)
                    .set(data)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Exception:", "Could not user document: ${exception.localizedMessage} ")
                    }


            }
            .addOnFailureListener { exception ->
                Log.e("Exception:", "Could not create user:  ${exception.localizedMessage}")
            }
    }

    fun createCancelClicked(view: View){
        finish()

    }
}
