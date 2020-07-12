package kumeda.cloudfirestore.Model

import java.util.*

class Comment constructor (val username: String, val timestamp: Date, val commentTxt: String,
                           val documentId :String,val userId :String)