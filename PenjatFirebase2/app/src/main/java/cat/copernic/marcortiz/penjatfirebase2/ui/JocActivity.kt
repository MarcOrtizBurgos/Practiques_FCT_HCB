package cat.copernic.marcortiz.penjatfirebase2.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_joc.*
import androidx.appcompat.app.AlertDialog
import cat.copernic.marcortiz.penjatfirebase2.R
import java.util.*
import java.util.concurrent.ExecutionException
import com.google.firebase.firestore.DocumentSnapshot
import kotlin.properties.Delegates

class JocActivity : AppCompatActivity() {

    val db = Firebase.firestore
    private var lletresprovades = ""
    private var checko = false
    private var bool = false
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joc)

        val bundle = intent.extras
        email = bundle?.getString("email").toString()
        val partida = bundle?.getString("partida")

        title = getString(R.string.joc)
        textUsuari2.text = email

        db.collection("jocs")
            .get()
            .addOnSuccessListener { result -> //Inicia les dades del joc
                for (document in result) {
                    if (document.id.equals(partida.toString())) {
                        textpartida.setText(document.id)
                        intents.setText(document.get("intents").toString())
                        acerts.setText(document.get("acerts").toString())
                        lletresprovades = document.get("lletresProvades").toString()
                        iniciar(document)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        buttonEnvia.setOnClickListener { //Inicia les comprovacions del joc al clicar
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

        buttonTanca2.setOnClickListener { //Tanca la sessió
            FirebaseAuth.getInstance().signOut()
            moveTaskToBack(true)
            val mainIntent: Intent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
    }

    //Inicia els valors visualment
    private fun iniciar(document: DocumentSnapshot) {
        var lletreambespai = ""
        val adivina = document.get("adivinar").toString() as String
        val paraula = document.get("paraula").toString() as String
        val ArraySep = paraula.split("").toTypedArray()
        for (i in ArraySep.indices) {
            if (adivina.toString().contains(ArraySep[i].toString())) {
                lletreambespai += ArraySep[i].toString() + " "
            } else {
                lletreambespai += "_ "
            }
        }
        paraulatext.setText(lletreambespai.toString())
        imagenes(document)
    }

    fun newjoc(partida: String, document: DocumentSnapshot) {
        //Iniciar si es te que iniciar algun valor
        //Primer fer comprovacio de la lletra
        //Depenent del tipus de lletra if si es valida else no
        //Si es valida s'actualitzan valors acerts +1
        //Si es negativa s'actualitzen valors intents -1
        //Ultim actualitzar els valors per comprovació
        //Comprovació si has guanyat , perdut o pots seguir

        var lletreambespai = ""
        val adivina = document.get("adivinar").toString()
        val paraula = document.get("paraula").toString()
        val ArraySep = paraula.split("").toTypedArray()
        val ArrayCon = arrayOfNulls<String>(ArraySep.size)

        //Pregunta lletra.
        val lletra: String = editLletra.text.toString().lowercase()

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

        checko = false

        println(lletresprovades)
        if (oneCharacter(lletra)) {
            lletresprovades += lletra[0]
            println(lletresprovades)
            //Recorreix la array per comprobar si hi ha lletras que coincideixen.
            var numAcerts = 0
            for (x in ArraySep.indices) {
                if (ArraySep[x].toString().contains(lletra.toString())) {
                    numAcerts += 1
                    ArrayCon[x] = lletra.toString()
                    checko = true
                    vibracioN(true)
                }
            }

            db.collection("jocs")
                .get()
                .addOnSuccessListener { result ->
                    for (doc in result) { //Si coincideix amb una lletra et posa un acert.
                        if (doc.id.equals(partida)) {
                            updateData(
                                partida.toString(),
                                doc.get("intents").hashCode() as Int,
                                doc.get("acerts").hashCode() + numAcerts,
                                doc.get("adivinar")
                                    .toString() + lletra.toString(),
                                lletresprovades
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
        if (!bool) {
            vibracioN(false)
            imagenes(document)
            updateData(
                partida.toString(),
                document.get("intents").hashCode(),
                document.get("acerts").hashCode(),
                document.get("adivinar").toString(),
                lletresprovades
            )
            intents.setText(document.get("intents").toString())
            acerts.setText(document.get("acerts").toString())

        }else if (!checko) {
            vibracioN(false)
            imagenes(document)
            updateData(
                partida.toString(),
                document.get("intents").hashCode() as Int - 1,
                document.get("acerts").hashCode(),
                document.get("adivinar").toString() + lletra.toString(),
                lletresprovades
            )
            intents.setText(document.get("intents").toString())
            acerts.setText(document.get("acerts").toString())
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
                                if (docu.get("intents")
                                        .hashCode() as Int == 0
                                ) { //Comprovació de que has perdut
                                    alert("Failure", getString(R.string.has_perdut_la_teva_paraula_era, paraula))
                                    finalitzat(partida, "perdut", 0)
                                    buttonEnvia.setEnabled(false)
                                    editLletra.setEnabled(false)
                                    val newinIntent: Intent =
                                        Intent(this, FailActivity::class.java).apply {
                                            putExtra("user", email.split("@")[0])
                                            putExtra("paraula", docu.get("paraula").toString())
                                            putExtra("punts", docu.get("intents").toString())
                                        }
                                    startActivity(newinIntent)
                                } else if (docu.get("acerts") //Comprovació de que has guanyat
                                        .hashCode() as Int == ArraySep?.filter { it.isNotEmpty() }.size.hashCode()
                                ) {
                                    //alert("Win", "Has guanyat! :) , la paraula es $paraula")
                                    finalitzat(
                                        partida,
                                        "guanyat",
                                        docu.get("intents").toString().toInt()
                                    )
                                    buttonEnvia.setEnabled(false)
                                    editLletra.setEnabled(false)
                                    val newinIntent: Intent =
                                        Intent(this, WinActivity::class.java).apply {
                                            putExtra("user", email.split("@")[0])
                                            putExtra("paraula", docu.get("paraula").toString())
                                            putExtra("punts", docu.get("intents").toString())
                                        }
                                    startActivity(newinIntent)
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            },
            1000
        )
        editLletra.text = null
    }

    //Finalitza la partida a la base de dades
    fun finalitzat(nomp: String?, estat: String, punts: Int) {
        val docRef = db.collection("jocs").document(nomp!!)
        val data: MutableMap<String, Any> = HashMap()
        data["state"] = true
        data["estatjoc"] = estat
        docRef.update(data)

        val docRef2 = db.collection("users").document(email!!)
        docRef2.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val data2: Map<String, Any> = hashMapOf(
                        "punts" to (document.get("punts").toString().toInt() + punts).toInt()
                    )
                    docRef2.update(data2)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    //Actualitza els resultats que hi ha al joc
    @Throws(InterruptedException::class, ExecutionException::class)
    private fun updateData(
        id: String?,
        intents: Int,
        acerts: Int,
        adivina: String,
        lletresprovades: String
    ) {
        val docRef = db.collection("jocs").document(id!!)
        val data: MutableMap<String, Any> = HashMap()
        data["acerts"] = acerts
        data["intents"] = intents
        data["adivinar"] = adivina
        data["lletresProvades"] = lletresprovades
        docRef.update(data)
    }

    //Switch d'imatges del joc penjat
    private fun imagenes(result: DocumentSnapshot) {
        when (result.get("intents").toString().toInt()) {
            0 -> imageView.setImageResource(R.drawable.ahorcado_8_png)
            1 -> imageView.setImageResource(R.drawable.ahorcado_7_png)
            2 -> imageView.setImageResource(R.drawable.ahorcado_6_png)
            3 -> imageView.setImageResource(R.drawable.ahorcado_5_png)
            4 -> imageView.setImageResource(R.drawable.ahorcado_4_png)
            5 -> imageView.setImageResource(R.drawable.ahorcado_3_png)
            6 -> imageView.setImageResource(R.drawable.ahorcado_2_png)
            7 -> imageView.setImageResource(R.drawable.ahorcado_1_png)
            else -> alert(getString(R.string.error), getString(R.string.error_d_imatge))
        }
    }

    //Funcio per mostrar alerts
    private fun alert(title: String, alert: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(alert)
        builder.setPositiveButton(getString(R.string.acceptar), null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun oneCharacter(lletra: String): Boolean {
        return if (Character.isLetter(lletra[0])) {
            val c = lletra[0].toString()
            if (repeatedLetter(c)) {
                Toast.makeText(this, getString(R.string.aquesta_lletra_ja_l_has_provat), Toast.LENGTH_SHORT).show()
                bool = false
                false
            } else {
                bool = true
                true
            }
        } else {
            Toast.makeText(this, getString(R.string.el_caracter_introduit_no_es_una_lletra), Toast.LENGTH_SHORT)
                .show()
            bool = false
            false
        }
    }

    private fun repeatedLetter(c: String): Boolean {
        var repeatedLetter = false
        var i = 0
        while (i < lletresprovades.length) {
            if (lletresprovades[i].toString() == c) {
                repeatedLetter = true
            }
            i++
        }

        return repeatedLetter
    }

    private fun vibracioN(value : Boolean) {
        var sonido : MediaPlayer? = null
        if(value){
            sonido = MediaPlayer.create(this, R.raw.acert)
        } else{
            sonido = MediaPlayer.create(this, R.raw.error)
        }
        sonido.start()
        val v = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 1000, 300)
        v.vibrate(pattern, -1)
        v.cancel()
    }


}