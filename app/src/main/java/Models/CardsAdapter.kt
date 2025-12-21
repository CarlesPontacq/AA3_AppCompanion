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

// RecyclerView adapter used to display a list of Pokemon cards
class PokemonCardAdapter(
    private val cards: List<PokemonCard>
) : RecyclerView.Adapter<PokemonCardAdapter.CardViewHolder>() {

    // Creates a new ViewHolder when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_list_card, parent, false)
        return CardViewHolder(view)
    }

    // Binds data to the ViewHolder at the given position
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    // Returns the total number of items
    override fun getItemCount(): Int = cards.size

    // ViewHolder that represents a single card item
    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image: ImageView = itemView.findViewById(R.id.cardImage)

        // Loads the card image asynchronously and displays it
        fun bind(card: PokemonCard){

            // Run network operation in a background thread
            thread {
                try {
                    val url = URL(card.images.small)
                    val bitmap: Bitmap = BitmapFactory.decodeStream(url.openStream())

                    // Update UI on the main thread
                    itemView.post{
                        image.setImageBitmap(bitmap)
                    }
                }
                catch (e: Exception){
                    // Log image loading errors
                    e.printStackTrace()
                }
            }
        }
    }
}