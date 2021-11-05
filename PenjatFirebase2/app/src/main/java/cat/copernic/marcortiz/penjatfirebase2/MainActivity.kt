package cat.copernic.marcortiz.penjatfirebase2

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de firebase completa")
        analytics.logEvent("InitScreen", bundle)

        setup()
    }

    private fun setup(){
        title = "Autenticatió"
        buttonRegistre.setOnClickListener{
            println(editUsuari.text.toString())
            println(editClau.text.toString())
            if(editUsuari.text.isNotEmpty() && editClau.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(editUsuari.text.toString(),
                        editClau.text.toString()).addOnCompleteListener(){
                            if (it.isSuccessful){
                                showPenjat(it.result?.user?.email ?: "")
                            } else{
                                showAlert()
                            }
                    }
            }
        }

        buttonLogin.setOnClickListener{
            if(editUsuari.text.isNotEmpty() && editClau.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(editUsuari.text.toString(),
                        editClau.text.toString()).addOnCompleteListener(){
                        if (it.isSuccessful){
                            showPenjat(it.result?.user?.email ?: "")
                        } else{
                            showAlert()
                        }
                    }
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se a produit un error autenticant l'usuari")
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    private fun showPenjat(email : String){
        val penjatIntent : Intent= Intent(this,FragmentPenjat::class.java).apply {
            putExtra("email",email)
        }
        startActivity(penjatIntent)
    }
}