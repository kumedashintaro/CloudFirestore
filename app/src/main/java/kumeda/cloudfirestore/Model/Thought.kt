package kumeda.cloudfirestore.Model


import java.util.Date


data class Thought constructor(val username: String, val timestamp: Date, val thoughtTxt: String,
                               val numLikes: Int, val NumComments: Int, val documentId: String, val userId: String)