package cat.copernic.marcortiz.penjatfirebase2

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_joc.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import java.util.*
import java.util.concurrent.ExecutionException
import com.google.firebase.firestore.DocumentSnapshot

class JocActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joc)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val partida = bundle?.getString("partida")

        title = "Joc"
        textUsuari2.text = email

        db.collection("jocs")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id.toString().equals(partida.toString())){
                        textpartida.setText(document.id)
                        intents.setText(document.get("intents").toString())
                        acerts.setText(document.get("acerts").toString())
                        iniciar(document)
                    }
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
                            newjoc(partida, document)
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

    private fun iniciar(document: DocumentSnapshot){
        var lletreambespai = ""
        val adivina = document.get("adivinar").toString() as String
        val paraula = document.get("paraula").toString() as String
        val ArraySep = paraula.split("").toTypedArray()
        for (i in ArraySep.indices) {
            if (adivina.toString().contains(ArraySep[i].toString())) {
                lletreambespai += ArraySep[i].toString()+" "
            } else {
                lletreambespai += "_ "
            }
        }
        paraulatext.setText(lletreambespai.toString())
        imagenes(document)
    }

    public fun newjoc(partida: String, document: DocumentSnapshot) {
        //Iniciar si es te que iniciar algun valor
        //Primer fer comprovacio de la lletra
        //Depenent del tipus de lletra if si es valida else no
        //Si es valida s'actualitzan valors acerts +1
        //Si es negativa s'actualitzen valors intents -1
        //Ultim actualitzar els valors per comprovació
        //Comprovació si has guanyat , perdut o pots seguir

        var lletreambespai = ""
        val adivina = document.get("adivinar").toString() as String
        val paraula = document.get("paraula").toString() as String
        val ArraySep = paraula.split("").toTypedArray()
        val ArrayCon = arrayOfNulls<String>(ArraySep.size)

        //Pregunta lletra.
        val lletra: String = editLletra.text.toString()

        for (i in ArraySep.indices) {
            if (adivina.toString().contains(ArraySep[i].toString())) {
                lletreambespai += ArraySep[i].toString() + " "
                ArrayCon[i] = ArraySep[i].toString()
            } else {
                lletreambespai += "_ "
                ArrayCon[i] = "_"
            }
        }
        paraulatext.setText(lletreambespai.toString())
        imagenes(document)

        var rep = false
        //Comproba que ya esta posada o no a l'array
        for (x in ArrayCon.indices) {
            if (ArrayCon[x].toString().contains(lletra.toString())) {
                rep = true
            }
        }

        var checko = false

        if (rep) {
            alert("Error","Aquest caracter ja l'has posat")
        } else {
            //Si es una lletra segueix el joc sino es torna a preguntar.
            if (!Character.isLetter(lletra[0])) {
                alert("Error","Aquest caracter no es correcte.\nTorna a probar.")
            } else {
                //Recorreix la array per comprobar si hi ha lletras que coincideixen.
                var numAcerts = 0
                for (x in ArraySep.indices) {
                    if (ArraySep[x].toString().contains(lletra.toString())) {
                        numAcerts += 1
                        ArrayCon[x] = lletra.toString()
                        checko = true
                    }
                }

                db.collection("jocs")
                    .get()
                    .addOnSuccessListener { result ->
                        for (doc in result) {
                            if (doc.id.equals(partida)) {
                                updateData(
                                    partida.toString(),
                                    doc.get("intents").hashCode() as Int,
                                    doc.get("acerts").hashCode() + numAcerts,
                                    doc.get("adivinar")
                                        .toString() + lletra.toString()
                                )
                                intents.setText(doc.get("intents").toString())
                                acerts.setText(doc.get("acerts").toString())
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            }
            //Si no coincideix ninguna lletra et treu un intent.
            if (!checko) {
                imagenes(document)
                updateData(
                    partida.toString(),
                    document.get("intents").hashCode() as Int - 1,
                    document.get("acerts").hashCode(),
                    document.get("adivinar").toString() + lletra.toString()
                )
                intents.setText(document.get("intents").toString())
                acerts.setText(document.get("acerts").toString())
            }

        }
        Handler().postDelayed(
            {
                db.collection("jocs")
                    .get()
                    .addOnSuccessListener { result ->
                        for (docu in result) {
                            if (docu.id.toString().equals(partida.toString())) {
                                textpartida.setText(docu.id)
                                intents.setText(docu.get("intents").toString())
                                acerts.setText(docu.get("acerts").toString())
                                iniciar(docu)

                                if (docu.get("intents").hashCode() as Int == 0) {
                                    alert("Failure","Has perdut :( , la teva paraula era $paraula")
                                    finalitzat(partida, "perdut")
                                } else if (docu.get("acerts")
                                        .hashCode() as Int == ArraySep?.filter { it.isNotEmpty() }.size.hashCode()
                                ) {
                                    alert("Win","Has guanyat! :) , la paraula es $paraula")
                                    finalitzat(partida, "guanyat")
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            },
            2000 // value in milliseconds
        )
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
        val data: MutableMap<String,Any> = HashMap()
        data["acerts"] = acerts
        data["intents"] = intents
        data["adivinar"] = adivina
        docRef.update(data)
    }

    fun setImageViewResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    private fun imagenes(result : DocumentSnapshot){
        when (result.get("intents").toString().toInt()) {
            0 -> {setImageViewResource(imageView, R.drawable.ahorcado_8_png)}
            1 -> {setImageViewResource(imageView, R.drawable.ahorcado_7_png)}
            2 -> {setImageViewResource(imageView, R.drawable.ahorcado_6_png)}
            3 -> {setImageViewResource(imageView, R.drawable.ahorcado_5_png)}
            4 -> {setImageViewResource(imageView, R.drawable.ahorcado_4_png)}
            5 -> {setImageViewResource(imageView, R.drawable.ahorcado_3_png)}
            6 -> {setImageViewResource(imageView, R.drawable.ahorcado_2_png)}
            7 -> {setImageViewResource(imageView, R.drawable.ahorcado_1_png)}
            else -> { // Note the block
                print("Ta mal algo")
            }
        }
    }

    private fun alert(title : String,alert : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(alert)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}