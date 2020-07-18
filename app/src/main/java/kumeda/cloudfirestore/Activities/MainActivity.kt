package kumeda.cloudfirestore.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kumeda.cloudfirestore.*
import kumeda.cloudfirestore.Adapters.ThoughtsAdapter
import kumeda.cloudfirestore.Model.Thought
import kumeda.cloudfirestore.R
import kumeda.cloudfirestore.Utillites.*
import kumeda.cloudfirestore.`interface`.ThoughtOptinsClickListener
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), ThoughtOptinsClickListener {


    var selectedCategory = FUNNY
    lateinit var thoughtsAdapter: ThoughtsAdapter
    val thoughts = arrayListOf<Thought>()
    val thoughtsCollectionRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
    lateinit var thoughtsListener : ListenerRegistration
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val addThoughtIntent = Intent(this, AddThoughtActivity::class.java)
            startActivity(addThoughtIntent)
        }

        thoughtsAdapter = ThoughtsAdapter(thoughts,this) {thought ->
            val commentsActivity = Intent(this, CommentsActivity::class.java)
            commentsActivity.putExtra(DOCUMENT_KEY, thought.documentId)
            startActivity(commentsActivity)

        }
        thoughtListView.adapter = thoughtsAdapter
        val layoutManager = LinearLayoutManager(this)
        thoughtListView.layoutManager = layoutManager
        auth = FirebaseAuth.getInstance()

    }

    override fun onResume(){
        super.onResume()
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuItem = menu.getItem(0)
        if(auth.currentUser == null) {
            menuItem.title = "Login"

        } else {
            menuItem.title = "Logout"
        }
        return super.onPrepareOptionsMenu(menu)
    }


    override fun thoughtOptionsMenunClicked(thought: Thought) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.options_menu,null)
        val deleteBtn = dialogView.findViewById<Button>(R.id.optionDeleteBtn)
        val editBtn = dialogView.findViewById<Button>(R.id.optionEditBtn)

        builder.setView(dialogView)
            .setNegativeButton("Cancel"){ _, _ ->}
        val ad = builder.show()

        deleteBtn.setOnClickListener {
            val thoughtRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                .document(thought.documentId)
            val collectionRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                .document(thought.documentId)
                .collection(COMMENTS_REF)

            deleteCollection(collectionRef, thought) { success ->
                if (success) {
                    thoughtRef.delete()
                        .addOnSuccessListener {
                            ad.dismiss()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Exception", "Could not delete though: $exception")
                        }
                }
            }
        }

        editBtn.setOnClickListener {

        }

    }

    fun deleteCollection(collection: CollectionReference, thought: Thought, complete: (Boolean) -> Unit) {
        collection.get().addOnSuccessListener { snapshot ->
            thread{
                val batch = FirebaseFirestore.getInstance().batch()
                for (document in snapshot) {
                    val docRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thought.documentId)
                        .collection(COMMENTS_REF).document(document.id)
                    batch.delete(docRef)
                }
                batch.commit()
                    .addOnSuccessListener{
                        complete(true)
                    }
                    .addOnFailureListener{exception ->
                        Log.e("Exception", "Could not delete subcollection: $exception")
                    }
            }
        }.addOnFailureListener { exception ->
            Log.e("Exception", "Could not retrieve documents: $exception")
        }
    }


    fun updateUI() {
        if (auth.currentUser == null){
            mainCrazyBtn.isEnabled = false
            mainPopurlarBtn.isEnabled = false
            mainFunnyBtn.isEnabled = false
            mainSeriousBtn.isEnabled = false
            fab.isEnabled = false
            thoughts.clear()
            thoughtsAdapter.notifyDataSetChanged()

        } else{
            mainCrazyBtn.isEnabled = true
            mainPopurlarBtn.isEnabled = true
            mainFunnyBtn.isEnabled = true
            mainSeriousBtn.isEnabled = true
            fab.isEnabled = true
            setListener()

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_login) {
            if (auth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }else{
                auth.signOut()
                updateUI()
            }
            return true
        }
        return false
    }

    fun setListener(){

        if (selectedCategory == POPULAR){
            thoughtsListener = thoughtsCollectionRef
                .orderBy(NUM_LIKES, Query.Direction.DESCENDING)
                .addSnapshotListener(this)  { snapshot, exception ->

                    if (exception != null){
                        Log.e("Exception", "Could not retrieve documents: $exception")
                    }

                    if (snapshot != null) {
                        paresData(snapshot)

                    }
                }

        }else{
            thoughtsListener = thoughtsCollectionRef
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(CATEGORY, selectedCategory)
                .addSnapshotListener(this)  { snapshot, exception ->

                    if (exception != null){
                        Log.e("Exception", "Could not retrieve documents: $exception")
                    }

                    if (snapshot != null) {
                        paresData(snapshot)
                    }
                }
        }
    }

    fun paresData(snapshot: QuerySnapshot) {
        thoughts.clear()
        for (document in snapshot.documents) {
            val data = document.data
            val name = data?.get(USERNAME) as String
            val timestamp = data[TIMESTAMP] as Timestamp
            val thoughtTxt = data[THOUGHT_TXT] as? String
            val numLikes = data[NUM_LIKES] as Long
            var numComments = data[NUM_COMMENTS] as Long
            val documentId = document.id
            val userId = data[USER_ID] as? String

            if(numComments == null) numComments = 0

            val newThought = Thought(
                name,
                timestamp.toDate(),
                thoughtTxt.toString(),
                numLikes.toInt(),
                numComments.toInt(),
                documentId,
                userId.toString()
            )

            thoughts.add(newThought)
        }
        thoughtsAdapter.notifyDataSetChanged()

    }

    fun mainFunnyClicked(view: View) {
        if (selectedCategory == FUNNY) {
            mainFunnyBtn.isChecked = true
            return
        }
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        mainPopurlarBtn.isChecked = false
        selectedCategory = FUNNY

        thoughtsListener.remove()
        setListener()
    }

    fun mainSeriousClicked(view: View) {
        if (selectedCategory == SERIOUS) {
            mainFunnyBtn.isChecked = true
            return
        }
        mainFunnyBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        mainPopurlarBtn.isChecked = false
        selectedCategory = SERIOUS

        thoughtsListener.remove()
        setListener()

    }

    fun mainCrazyClicked(view: View) {
        if (selectedCategory == CRAZY) {
            mainFunnyBtn.isChecked = true
            return
        }
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = false
        mainPopurlarBtn.isChecked = false
        selectedCategory = CRAZY

        thoughtsListener.remove()
        setListener()
    }

    fun mainPopularClicked(view: View) {
        if (selectedCategory == POPULAR) {
            mainFunnyBtn.isChecked = true
            return
        }
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        selectedCategory = POPULAR

        thoughtsListener.remove()
        setListener()
    }
}
