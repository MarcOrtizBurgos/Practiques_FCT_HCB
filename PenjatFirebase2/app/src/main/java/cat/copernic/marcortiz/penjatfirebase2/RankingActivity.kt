package cat.copernic.marcortiz.penjatfirebase2

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.marcortiz.penjatfirebase2.adapters.RankingAdapter
import cat.copernic.marcortiz.penjatfirebase2.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_ranking.*
import kotlin.collections.ArrayList


class RankingActivity : AppCompatActivity() {

    val db = Firebase.firestore

    private lateinit var usuaris: ArrayList<User>
    private lateinit var myAdapter: RankingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.fragment_ranking)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        usuaris = arrayListOf()

        iniciador()

    }

    private fun iniciador() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.get("id").toString().isNotEmpty() && document.get("date").toString().isNotEmpty()
                        && document.get("punts").toString().isNotEmpty()) {
                        usuaris.add(User(document.get("id").toString(), document.get("date").toString(),document.get("punts").toString()))
                    }
                }
                myAdapter = RankingAdapter(usuaris)
                recyclerView.adapter = myAdapter
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }
}