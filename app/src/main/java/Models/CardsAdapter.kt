package Models

import PokemonApi.PokemonCard
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcompanion.R
import java.net.URL
import kotlin.concurrent.thread

class PokemonCardAdapter(
    private val cards: List<PokemonCard>
) : RecyclerView.Adapter<PokemonCardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_list_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image: ImageView = itemView.findViewById(R.id.cardImage)

        fun bind(card: PokemonCard){

            thread {
                try {
                    val url = URL(card.images.small)
                    val bitmap: Bitmap = BitmapFactory.decodeStream(url.openStream())

                    itemView.post{
                        image.setImageBitmap(bitmap)
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }
}