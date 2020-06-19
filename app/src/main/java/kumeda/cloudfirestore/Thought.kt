package kumeda.cloudfirestore

import com.google.firebase.Timestamp
import java.util.Date


data class Thought constructor(val username: String, val timestamp: Date, val thoughtTxt: String,
                               val numLikes: Int, val NumComments: Int, val documentId: String)