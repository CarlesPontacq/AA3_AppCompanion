package com.example.appcompanion

import Models.PokemonType
import PokemonApi.DetailedPokemonCard
import PokemonApi.PokemonApiCall
import PokemonApi.SinglePokemonCardResponse
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import kotlin.concurrent.thread

class CardInfoFragment : Fragment() {
    private var cardId: String? = null

    private lateinit var loadingBar: ProgressBar
    private lateinit var loadingOverlay: FrameLayout

    private lateinit var cardImage: ImageView
    private lateinit var cardSubtypeText: TextView
    private lateinit var cardTypeImage: ImageView
    private lateinit var cardHpText: TextView
    private lateinit var cardRetreatCostLayout: LinearLayout
    private lateinit var attackNameText: TextView
    private lateinit var attackDamageText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cardId = arguments?.getString("card_id")

        val view = inflater.inflate(R.layout.fragment_card_info, container, false)

        loadingBar = view.findViewById(R.id.cardInfoProgressBar)
        loadingOverlay = view.findViewById(R.id.loadingOverlay)

        cardImage = view.findViewById(R.id.detailedCardImage)
        cardTypeImage = view.findViewById(R.id.cardType)
        cardSubtypeText = view.findViewById(R.id.cardSubtypes)
        cardHpText = view.findViewById(R.id.cardHp)
        cardRetreatCostLayout = view.findViewById(R.id.cardRetreatCost)
        attackNameText = view.findViewById(R.id.attackName)
        attackDamageText = view.findViewById(R.id.attackDamage)

        // If the card ID is valid, load the card
        cardId?.let { loadCard(it) }

        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Loading..."
    }

    private fun showLoading() {
        loadingBar.visibility = View.VISIBLE
        loadingOverlay.visibility = View.VISIBLE
    }

    private fun showContent() {
        loadingBar.visibility = View.GONE
        loadingOverlay.visibility = View.GONE
    }

    private fun loadCard(id: String) {
        showLoading()

        val call = PokemonApiCall.apiService.getCard(id)

        call.enqueue(object : Callback<SinglePokemonCardResponse> {

            override fun onResponse(
                call: Call<SinglePokemonCardResponse>,
                response: Response<SinglePokemonCardResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("PokemonDetailedCard", "Successful call")
                    response.body()?.data?.let { displayCardInfo(it) }
                    showContent()
                }
                else {
                    Log.d("PokemonDetailedCard", "Unsuccessful call")
                }
            }

            override fun onFailure(call: Call<SinglePokemonCardResponse>, t: Throwable) {
                // FALTA POR PONER
            }
        })
    }

    private fun displayCardInfo(card: DetailedPokemonCard)
    {
        thread {
            try {
                val cardImageUrl = URL(card.images.large)
                val cardImageBitmap: Bitmap = BitmapFactory.decodeStream(cardImageUrl.openStream())

                cardImage.post {
                    cardImage.setImageBitmap(cardImageBitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        cardSubtypeText.text = card.subtypes?.joinToString(" ") ?: "-"

        val typeString = card.types?.firstOrNull()
        val typeEnum = PokemonType.fromString(typeString)
        cardTypeImage.setImageResource(typeEnum.drawableRes)

        cardHpText.text = card.hp

        card.retreatCost?.forEachIndexed { index, costType ->
            if (index < cardRetreatCostLayout.childCount) {
                val imageView = cardRetreatCostLayout.getChildAt(index) as ImageView
                val retreatTypeEnum = PokemonType.fromString(costType)

                imageView.setImageResource(retreatTypeEnum.drawableRes)
                imageView.visibility = View.VISIBLE
            }
        }

        val firstAttack = card.attacks?.firstOrNull()
        attackNameText.text = firstAttack?.name ?: "-"
        attackDamageText.text = firstAttack?.damage?.toString() ?: "-"

        (activity as? AppCompatActivity)?.supportActionBar?.title = card.name
    }
}