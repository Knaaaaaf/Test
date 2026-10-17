package com.kna.sigma

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private var player: MediaPlayer? = null
    private var selectedUri: Uri? = null
    private lateinit var playButton: Button
    private lateinit var status: TextView
    private val lime = Color.rgb(199, 255, 61)
    private val picker = 41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 9, 13)
        window.navigationBarColor = Color.rgb(8, 9, 13)
        buildScreen()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 36, 28, 28)
            setBackgroundColor(Color.rgb(8, 9, 13))
        }
        root.addView(TextView(this).apply {
            text = "●  SYSTEM ONLINE"
            setTextColor(lime); textSize = 12f; letterSpacing = .18f; gravity = Gravity.CENTER
        }, LinearLayout.LayoutParams(-1, 48))
        root.addView(TextView(this).apply {
            text = "σ"; textSize = 112f; setTextColor(lime); gravity = Gravity.CENTER
            typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
        }, LinearLayout.LayoutParams(-1, 190))
        root.addView(TextView(this).apply {
            text = "sigma"; textSize = 54f; setTextColor(Color.WHITE); letterSpacing = .16f
            gravity = Gravity.CENTER; typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
        }, LinearLayout.LayoutParams(-1, 82))
        root.addView(TextView(this).apply {
            text = "STAY IN YOUR LANE"; textSize = 11f; setTextColor(Color.LTGRAY)
            letterSpacing = .28f; gravity = Gravity.CENTER
        }, LinearLayout.LayoutParams(-1, 48))
        root.addView(View(this), LinearLayout.LayoutParams(1, 36))
        playButton = Button(this).apply {
            text = "▶   ODPAL MUZYKĘ"; setTextColor(Color.rgb(15, 18, 8)); textSize = 14f
            typeface = Typeface.DEFAULT_BOLD; setBackgroundColor(lime); isAllCaps = false
            setOnClickListener { togglePlayback() }
        }
        root.addView(playButton, LinearLayout.LayoutParams(-1, 58))
        val choose = Button(this).apply {
            text = "＋  Wybierz muzykę z telefonu"; setTextColor(Color.WHITE); textSize = 14f
            isAllCaps = false; setBackgroundColor(Color.rgb(32, 35, 40))
            setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE); type = "audio/*"
                }
                startActivityForResult(intent, picker)
            }
        }
        root.addView(choose, LinearLayout.LayoutParams(-1, 58).apply { topMargin = 14 })
        status = TextView(this).apply {
            text = "Wybierz utwór i odpal sigma mode."; textSize = 12f
            setTextColor(Color.LTGRAY); gravity = Gravity.CENTER
        }
        root.addView(status, LinearLayout.LayoutParams(-1, 68))
        root.addView(TextView(this).apply {
            text = "MADE FOR MOBILE · SIGMA"; textSize = 10f; setTextColor(Color.GRAY)
            letterSpacing = .16f; gravity = Gravity.CENTER
        }, LinearLayout.LayoutParams(-1, 48))
        setContentView(root)
    }

    private fun togglePlayback() {
        try {
            val current = player
            if (current != null && current.isPlaying) {
                current.pause(); playButton.text = "▶   WZNÓW"; status.text = "Odtwarzanie wstrzymane."; return
            }
            if (current != null) {
                current.start(); playButton.text = "Ⅱ   PAUZA"; status.text = "Sigma mode: ON."; return
            }
            val uri = selectedUri
            if (uri == null) {
                status.text = "Najpierw wybierz plik muzyczny z telefonu."
                Toast.makeText(this, "Wybierz plik audio", Toast.LENGTH_SHORT).show(); return
            }
            player = MediaPlayer().apply {
                setDataSource(this@MainActivity, uri)
                setOnCompletionListener {
                    playButton.text = "▶   ODPAL MUZYKĘ"; status.text = "Koniec utworu."
                }
                prepare(); start()
            }
            playButton.text = "Ⅱ   PAUZA"; status.text = "Muzyka gra. Sigma mode: ON."
        } catch (e: Exception) {
            status.text = "Nie udało się odtworzyć pliku. Wybierz inny utwór."
            Toast.makeText(this, "Błąd odtwarzania audio", Toast.LENGTH_SHORT).show()
            player?.release(); player = null
        }
    }

    @Deprecated("Deprecated in Android API; retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == picker && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedUri = uri; player?.release(); player = null
                playButton.text = "▶   ODPAL MUZYKĘ"
                status.text = "Wybrano plik audio. Gotowy do odtworzenia."
            }
        }
    }

    override fun onDestroy() {
        player?.release(); player = null; super.onDestroy()
    }
}
