package cat.copernic.marcortiz.penjatfirebase2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.marcortiz.penjatfirebase2.R
import cat.copernic.marcortiz.penjatfirebase2.models.User

class RankingAdapter(private val userList:ArrayList<User>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_ranking,viewGroup,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        /*
        viewHolder.itemTitle.text = titles[i]
        viewHolder.itemImage.setImageResource(R.drawable.ahorcado_8_png)
        viewHolder.itemDetail.text = details[i]*/
        val user : User = userList[i]
        viewHolder.itemImage.setImageResource(R.drawable.ahorcado_8_png)
        viewHolder.itemTitle.text = user.name
        viewHolder.itemDetail.text = user.date
        viewHolder.itemPunts.text = user.punts
    }


    override fun getItemCount(): Int {
        return userList.size
    }

    public class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView
        var itemPunts: TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detal)
            itemPunts = itemView.findViewById(R.id.item_punts)
        }

    }
}