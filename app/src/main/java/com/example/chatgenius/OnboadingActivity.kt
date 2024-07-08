package com.example.chatgenius

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class OnboadingActivity : AppCompatActivity() {

    private lateinit var inputQuestion: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var chatLayout: LinearLayout
    private lateinit var nestedScrollView: NestedScrollView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboading)

        // Inicijalizacija view-ova
        inputQuestion = findViewById(R.id.input_question)
        buttonSend = findViewById(R.id.button_send)
        chatLayout = findViewById(R.id.chat_layout)
        nestedScrollView = findViewById(R.id.nested_scroll_view)  // Dodajte ovu liniju za inicijalizaciju nestedScrollView

        val closeButton: ImageButton = findViewById(R.id.closeButton)

        // Postavljanje OnClickListener-a za ImageButton
        closeButton.setOnClickListener {
            // Kreiranje Intent-a za prelazak na MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Postavljanje OnClickListener-a na dugme za slanje
        buttonSend.setOnClickListener {
            sendMessage()
        }

        // Listener za submit inputQuestion
        inputQuestion.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun getRapidApi(questionText: String, callback: (String) -> Unit) {
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = "{\"init_character\":\"\",\"user_name\":\"Kile\",\"character_name\":\"Sahra\",\"text\":\"$questionText\"}".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://ai-api-textgen.p.rapidapi.com/completions")
            .post(body)
            .addHeader("x-rapidapi-key", "65f8e28fc9msh740f5c710731f09p18f138jsn6eeb3143879e")
            .addHeader("x-rapidapi-host", "ai-api-textgen.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure (e.g., log error, notify user)
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        // Handle unsuccessful response
                        throw IOException("Unexpected code $it")
                    }

                    val responseBody = it.body
                    if (responseBody != null) {
                        val responseString = responseBody.string()
                        // Invoke callback with the response
                        callback(responseString)
                    } else {
                        println("Response body is null")
                    }
                }
            }
        })
    }


    private fun displayResponse(response: String, initialBotTextView: TextView) {
        val regex = "(Kile:|Sahra:)".toRegex()
        val matchResult = regex.find(response)
        println(response)

        var textToShow = if (matchResult != null) {
            // Get the substring up to the first match
            response.substring(0, matchResult.range.first).trim()
        } else {
            // No match found, use the whole response
            response.trim()
        }

        // Remove the first character if it is a double quote
        if (textToShow.startsWith("\"")) {
            textToShow = textToShow.substring(1).trim()
        }
        // Remove the last character if it is a double quote
        if (textToShow.endsWith("\"")) {
            textToShow = textToShow.substring(0, textToShow.length - 1).trim()
        }
        // Replace \n with actual newlines
        textToShow = textToShow.replace("\\n", "\n")

        // Replace the last character if it is a backslash with a dot
        if (textToShow.endsWith("\\")) {
            textToShow = textToShow.substring(0, textToShow.length - 1).trim() + "."
        }

        Handler(Looper.getMainLooper()).post {
            // Update the initialBotTextView with the final text
            initialBotTextView.text = textToShow
            initialBotTextView.setBackgroundResource(R.color.purple) // Set the background color
            initialBotTextView.setTextColor(Color.BLACK) // Set the text color
            initialBotTextView.setPadding(16, 8, 16, 8) // Set padding

            // Update layout parameters
            val botLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            botLayoutParams.setMargins(16, 8, 16, 8) // Set margins (left, top, right, bottom)
            initialBotTextView.layoutParams = botLayoutParams
        }
    }






    private fun sendMessage() {
        // Dobijanje teksta iz EditText-a
        val questionText = inputQuestion.text.toString().trim()

        if (questionText.isNotEmpty()) {
            // Kreiranje novog LinearLayout-a za korisnički unos
            val userLayout = LinearLayout(this)
            userLayout.orientation = LinearLayout.HORIZONTAL

            // Dodavanje slike za korisnički unos
            val userImage = ImageView(this)
            userImage.setImageResource(R.drawable.user)
            val userImageParams = LinearLayout.LayoutParams(
                35.dpToPx(), 35.dpToPx() // Converting dp to pixels
            )
            userImageParams.topMargin = 16.dpToPx()
            userImage.layoutParams = userImageParams
            userLayout.addView(userImage)

            // Dodavanje TextView-a za korisnički unos
            val userTextView = TextView(this)
            userTextView.text = questionText
            userTextView.setBackgroundResource(R.drawable.user_background) // Postavljanje boje pozadine
            userTextView.setPadding(20, 10, 20, 10)  // Postavljanje padding-a
            userTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)

            // Postavljanje margina za TextView korisničkog unosa
            val userLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            userLayoutParams.setMargins(16, 58, 16, 58) // Postavljanje margina (left, top, right, bottom)
            userTextView.layoutParams = userLayoutParams
            userLayout.addView(userTextView)

            // Dodavanje korisničkog unosa u chatLayout
            chatLayout.addView(userLayout)

            // Kreiranje novog LinearLayout-a za botText
            val botLayout = LinearLayout(this)
            botLayout.orientation = LinearLayout.HORIZONTAL

            // Dodavanje slike za botText (koristimo user)
            val botImage = ImageView(this)
            botImage.setImageResource(R.drawable.logo) // Promijenili smo izvor slike

            // Postavljanje layout parametara za sliku
            val botImageParams = LinearLayout.LayoutParams(
                35.dpToPx(), 35.dpToPx()
            )
            botImage.layoutParams = botImageParams
            botLayout.addView(botImage)


            // Kreiranje TextView-a za botText
            // Kreiranje TextView-a za botText
            val botTextView = TextView(this)
            botTextView.text = "wait..."
            botTextView.setBackgroundResource(R.drawable.bot_background) // Postavljanje boje pozadine za odgovor
            botTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white)) // Postavljanje boje teksta na bijelu
            botTextView.setPadding(20, 10, 20, 10) // Postavljanje padding-a
            botTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)


            // Postavljanje margina za TextView botText
            val botLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )// Postavljanje margina (left, top, right, bottom)
            botTextView.layoutParams = botLayoutParams
            botLayout.addView(botTextView)
            botLayoutParams.setMargins(16, 8, 16, 8)

            // Dodavanje botText u chatLayout
            chatLayout.addView(botLayout)

            // Poziv API-ja
            getRapidApi(questionText) { response ->
                // Procesiranje odgovora
                displayResponse(response, botTextView)
                nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener {
                    nestedScrollView.scrollTo(0, nestedScrollView.getChildAt(0).height)
                    botTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                    botLayoutParams.setMargins(16, 8, 16, 8)
                    botTextView.setPadding(20, 10, 20, 10) // Postavljanje padding-a
                    botTextView.setBackgroundResource(R.drawable.bot_background)
                }
            }
            // Brisanje unesenog teksta iz EditText-a nakon slanja
            inputQuestion.text.clear()
        }

        // Sakrivanje tipkovnice nakon slanja poruke
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputQuestion.windowToken, 0)
    }

    // Funkcija za pretvaranje dp u piksele
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()




}
