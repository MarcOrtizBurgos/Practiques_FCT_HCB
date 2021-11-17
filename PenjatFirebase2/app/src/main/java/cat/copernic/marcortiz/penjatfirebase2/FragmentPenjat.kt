package cat.copernic.marcortiz.penjatfirebase2

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_penjat.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import java.util.*
import java.util.Arrays.asList
import java.util.concurrent.ExecutionException
import kotlin.collections.HashMap
import kotlin.math.log


class FragmentPenjat : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_penjat)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        var partida = ""

        buttonN.setOnClickListener{
            partida = editpartida.text.toString()

            create(partida,"marc")
            println("Partida echa")

            val nouIntent : Intent = Intent(this,JocActivity::class.java).apply {
                putExtra("email",email)
                putExtra("partida",partida)
            }
            Handler().postDelayed(
                {
                    startActivity(nouIntent)
                },
                2000 // value in milliseconds
            )
        }

        buttonA.setOnClickListener{
            partida = editpartida.text.toString()
            val nouIntent : Intent = Intent(this,JocActivity::class.java).apply {
                putExtra("email",email)
                putExtra("partida",partida)
            }
            startActivity(nouIntent)
        }

        setup(email ?: "")
    }

    private fun setup(email : String){
        title = "Penjat"
        textUsuari.text = email

        buttonTanca.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

    //Busca una paraula random de l'array
    private fun paraulaRandom(paraules: String): String? {
        val replace = paraules.replace("[", "")
        val replace1 = replace.replace("]", "")
        val myList = Arrays.asList(replace1.split(", ").toTypedArray())
        val rand = Random()
        val num: Int = rand.nextInt(6)
        return myList[0][num]
    }

    //Crea una nova partida a la base de dades
    @Throws(InterruptedException::class, ExecutionException::class)
    private fun create(id: String?, usuari: String?) {
        db.collection("plantilla").document("penjat")
            .get()
            .addOnSuccessListener { result ->
                val docRef: DocumentReference = db.collection("jocs").document(id?:"")
                val data: MutableMap<String, Any?> = HashMap()
                data["acerts"] = result["acerts"].hashCode()
                data["adivinar"] = ""
                data["intents"] = result["intents"].hashCode()
                data["paraula"] = paraulaRandom(result["paraules"].toString()).toString()
                data["state"] = false
                data["estatjoc"] = ""
                data["usuari"] = usuari?:""
                docRef.set(data)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

}