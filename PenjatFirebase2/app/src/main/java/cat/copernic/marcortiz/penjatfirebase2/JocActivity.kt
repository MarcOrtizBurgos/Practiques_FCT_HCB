package cat.copernic.marcortiz.penjatfirebase2

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_joc.*
import android.provider.SyncStateContract.Helpers.update
import androidx.appcompat.app.AlertDialog
import java.util.*
import java.util.concurrent.ExecutionException
import com.google.firestore.v1.WriteResult

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot


class JocActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joc)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val partida = bundle?.getString("partida")
        setup(email ?: "",partida ?: "")
    }

    val db = Firebase.firestore

    private fun setup(email : String,partida : String){
        title = "Joc"
        textUsuari2.text = email
        //textpartida.text = partida.toString()

        db.collection("jocs")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //Log.d(TAG, "${document.id} => ${document.data}")
                    if (document.id.equals(partida)){
                        textpartida.setText(document.id)
                        intents.setText(document.data["intents"].toString())
                        acerts.setText(document.data["acerts"].toString())
                    }
                    Log.d("MainActivity", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }


        buttonEnvia.setOnClickListener{
            db.collection("jocs")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.id.equals(partida)) {
                            //Log.d(TAG, "${document.id} => ${document.data}")
                            joc(partida, document)
                            //Log.d("MainActivity", "${document.id} => ${document.data}")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }

        buttonTanca2.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            moveTaskToBack(true)
            val mainIntent : Intent = Intent(this,MainActivity::class.java)
            startActivity(mainIntent)
        }
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    fun joc(partida: String, document: DocumentSnapshot) {
        val `in` = Scanner(System.`in`)
        var joc = true
        val usuari: String = "marc"
        var nomp: String?
        nomp = partida
        //booleans dels do while de funcio del joc i de preguntar per jugar de nou
        var fin = false
        var altre = false
        //Escull paraula random de l'array.
        val paraula = document.get("paraula").toString() as String
        //Array de les lletras de la paraula.
        val ArraySep = paraula.split("").toTypedArray()
        //Array buit amb la longitud del primer array amb la paraula.
        val ArrayCon = arrayOfNulls<String>(ArraySep.size)
        //String adivina amb les lletres trobades
        val adivina = document.get("adivinar").toString() as String
        //For per posar amb _ l'array buida o les lletres que ja estan descobertes
        for (i in ArraySep.indices) {
            if (adivina.contains(ArraySep[i])) {
                ArrayCon[i] = ArraySep[i]
            } else {
                ArrayCon[i] = "_"
            }
        }
        var rep = false
        //Si perds o guanyes s'acaba l'ha partida sino segueix el joc
        if (document.get("intents").hashCode() as Int == 0) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Has perdut :( , la teva paraula era $paraula")
            builder.setPositiveButton("Aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            finalitzat(nomp, "perdut")
            fin = true
        } else if (document.get("acerts").hashCode() as Int == ArraySep.size) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Has guanyat! :) , la paraula es $paraula")
            builder.setPositiveButton("Aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            finalitzat(nomp, "guanyat")
            fin = true
        } else {
            //acerts i intents

            //Imprimeix l'array amb les lletres ocultes.
            var lletreambespai = ""
            for (i in ArraySep.indices) {
                if (ArrayCon[i] !== ArraySep[i]) {
                    lletreambespai += ArrayCon[i]+" "
                } else {
                    lletreambespai += ArraySep[i]+" "
                }
            }
            paraulatext.setText(lletreambespai)

            //Pregunta lletra.
            val lletra: String = editLletra.text.toString()

            //Comproba que ya esta posada o no a l'array
            for (x in ArrayCon.indices) {
                if (ArrayCon[x]!!.contains(lletra)) {
                    rep = true
                }
            }
            if (rep) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error")
                builder.setMessage("Aquest caracter ja l'has posat")
                builder.setPositiveButton("Aceptar", null)
                val dialog: AlertDialog = builder.create()
                dialog.show()
            } else {
                //Si es una lletra segueix el joc sino es torna a preguntar.
                if (!Character.isLetter(lletra[0])) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Error")
                    builder.setMessage(
                        "Aquest caracter no es correcte.\n"
                                + "Torna a probar."
                    )
                    builder.setPositiveButton("Aceptar", null)
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                } else {
                    //Recorreix la array per comprobar si hi ha lletras que coincideixen.
                    var check = false
                    for (x in ArraySep.indices) {
                        if (ArraySep[x].contains(lletra)) {
                            ArrayCon[x] = lletra
                            check = true
                            updateData(
                                nomp,
                                document.get("intents").hashCode() as Int,
                                document.get("acerts").hashCode() as Int + 1,
                                document.get("adivinar").toString() + lletra
                            )
                        }
                    }
                    //Si no coincideix ninguna lletra et treu un intent.
                    if (!check) {
                        updateData(
                            nomp,
                            document.get("intents").hashCode() as Int - 1,
                            document.get("acerts").hashCode(),
                            document.get("adivinar").toString() + lletra
                        )
                    }
                }
            }
        }
    }

    //Finalitza la partida a la base de dades
    fun finalitzat(nomp: String?, estat: String) {
        val docRef = db.collection("jocs").document(nomp!!)
        val data: MutableMap<String, Any> = HashMap()
        data["state"] = true
        data["estatjoc"] = estat
        docRef.update(data)
    }

    //Actualitza els resultats que hi ha al joc
    @Throws(InterruptedException::class, ExecutionException::class)
    private fun updateData(id: String?, intents: Int, acerts: Int, adivina: String) {
        val docRef = db.collection("jocs").document(id!!)
        val data: MutableMap<String, Any> = HashMap()
        data["acerts"] = acerts
        data["intents"] = intents
        data["adivinar"] = adivina
        docRef.update(data)
    }

}