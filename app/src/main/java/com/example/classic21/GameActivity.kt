package com.example.classic21

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var tvDealerCards: TextView
    private lateinit var tvDealerTotal: TextView
    private lateinit var tvPlayerCards: TextView
    private lateinit var tvPlayerTotal: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvWins: TextView
    private lateinit var tvLosses: TextView
    private lateinit var btnHit: Button
    private lateinit var btnStand: Button
    private lateinit var btnNewHand: Button
    private lateinit var btnBackToMenu: Button

    private val suits = listOf("♥", "♦", "♣", "♠")
    private val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

    private var deck = mutableListOf<Card>()
    private var playerHand = mutableListOf<Card>()
    private var dealerHand = mutableListOf<Card>()
    private var gameOver = false

    data class Card(val rank: String, val suit: String) {
        fun display(): String = "[ $rank$suit ]"
        fun value(): Int = when (rank) {
            "A" -> 11
            "K", "Q", "J" -> 10
            else -> rank.toInt()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        tvDealerCards = findViewById(R.id.tvDealerCards)
        tvDealerTotal = findViewById(R.id.tvDealerTotal)
        tvPlayerCards = findViewById(R.id.tvPlayerCards)
        tvPlayerTotal = findViewById(R.id.tvPlayerTotal)
        tvResult = findViewById(R.id.tvResult)
        tvWins = findViewById(R.id.tvWins)
        tvLosses = findViewById(R.id.tvLosses)
        btnHit = findViewById(R.id.btnHit)
        btnStand = findViewById(R.id.btnStand)
        btnNewHand = findViewById(R.id.btnNewHand)
        btnBackToMenu = findViewById(R.id.btnBackToMenu)

        updateScoreDisplay()

        btnHit.setOnClickListener { onHit() }
        btnStand.setOnClickListener { onStand() }
        btnNewHand.setOnClickListener { startNewHand() }
        btnBackToMenu.setOnClickListener { finish() }

        startNewHand()
    }

    private fun startNewHand() {
        gameOver = false
        tvResult.visibility = View.GONE
        btnNewHand.visibility = View.GONE
        btnHit.isEnabled = true
        btnStand.isEnabled = true

        deck = createShuffledDeck()
        playerHand.clear()
        dealerHand.clear()

        // Deal two cards each
        playerHand.add(drawCard())
        dealerHand.add(drawCard())
        playerHand.add(drawCard())
        dealerHand.add(drawCard())

        updateUI(hideDealer = true)
    }

    private fun onHit() {
        if (gameOver) return
        playerHand.add(drawCard())
        updateUI(hideDealer = true)

        if (handValue(playerHand) > 21) {
            endHand("Bust! You lose.")
            saveResult(won = false)
        }
    }

    private fun onStand() {
        if (gameOver) return
        // Dealer plays
        while (handValue(dealerHand) < 17) {
            dealerHand.add(drawCard())
        }
        updateUI(hideDealer = false)

        val playerVal = handValue(playerHand)
        val dealerVal = handValue(dealerHand)

        when {
            dealerVal > 21 -> {
                endHand("Dealer busts! You win!")
                saveResult(won = true)
            }
            playerVal > dealerVal -> {
                endHand("You win!")
                saveResult(won = true)
            }
            playerVal < dealerVal -> {
                endHand("You lose.")
                saveResult(won = false)
            }
            else -> {
                endHand("Push!")
                // no win/loss change on push
            }
        }
    }

    private fun endHand(message: String) {
        gameOver = true
        tvResult.text = message
        tvResult.visibility = View.VISIBLE
        btnHit.isEnabled = false
        btnStand.isEnabled = false
        btnNewHand.visibility = View.VISIBLE
        updateUI(hideDealer = false)
        updateScoreDisplay()
    }

    private fun updateUI(hideDealer: Boolean) {
        // Player
        tvPlayerCards.text = playerHand.joinToString("  ") { it.display() }
        tvPlayerTotal.text = "Total: ${handValue(playerHand)}"

        // Dealer
        if (hideDealer && dealerHand.isNotEmpty()) {
            val visible = dealerHand.drop(1).joinToString("  ") { it.display() }
            tvDealerCards.text = "[ ? ]  $visible"
            tvDealerTotal.text = "Total: ?"
        } else {
            tvDealerCards.text = dealerHand.joinToString("  ") { it.display() }
            tvDealerTotal.text = "Total: ${handValue(dealerHand)}"
        }
    }

    private fun handValue(hand: List<Card>): Int {
        var total = hand.sumOf { it.value() }
        var aces = hand.count { it.rank == "A" }
        while (total > 21 && aces > 0) {
            total -= 10
            aces--
        }
        return total
    }

    private fun createShuffledDeck(): MutableList<Card> {
        val newDeck = mutableListOf<Card>()
        for (suit in suits) {
            for (rank in ranks) {
                newDeck.add(Card(rank, suit))
            }
        }
        newDeck.shuffle()
        return newDeck
    }

    private fun drawCard(): Card {
        if (deck.isEmpty()) deck = createShuffledDeck()
        return deck.removeAt(0)
    }

    private fun saveResult(won: Boolean) {
        val prefs = getSharedPreferences("blackjack_prefs", Context.MODE_PRIVATE)
        val key = if (won) "wins" else "losses"
        val current = prefs.getInt(key, 0)
        prefs.edit().putInt(key, current + 1).apply()
    }

    private fun updateScoreDisplay() {
        val prefs = getSharedPreferences("blackjack_prefs", Context.MODE_PRIVATE)
        tvWins.text = "Wins: ${prefs.getInt("wins", 0)}"
        tvLosses.text = "Losses: ${prefs.getInt("losses", 0)}"
    }
}
