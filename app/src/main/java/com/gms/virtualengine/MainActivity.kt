package com.gms.virtualengine

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val navy = Color.rgb(8, 15, 35)
    private val blue = Color.rgb(35, 125, 255)
    private val white = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = navy
        window.navigationBarColor = navy

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 32, 22, 24)
            setBackgroundColor(navy)
        }

        val title = TextView(this).apply {
            text = "GMS VIRTUAL ENGINE"
            textSize = 25f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
        }
        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "Your virtual app management space"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 8, 0, 28)
        }
        root.addView(subtitle)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 22, 20, 22)
            background = rounded(Color.rgb(17, 29, 57), 22)
        }

        val status = TextView(this).apply {
            text = "ENGINE STATUS"
            textSize = 12f
            setTextColor(Color.LTGRAY)
        }
        card.addView(status)

        val ready = TextView(this).apply {
            text = "●  INITIALIZING"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(80, 170, 255))
            setPadding(0, 12, 0, 8)
        }
        card.addView(ready)

        val info = TextView(this).apply {
            text = "Virtual app manager foundation"
            textSize = 13f
            setTextColor(Color.LTGRAY)
        }
        card.addView(info)

        root.addView(card)

        val section = TextView(this).apply {
            text = "VIRTUAL SPACE"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
            setPadding(2, 30, 0, 14)
        }
        root.addView(section)

        val launch = Button(this).apply {
            text = "＋   Add Authorized App"
            textSize = 15f
            setTextColor(white)
            background = rounded(blue, 16)
            setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "App manager module is coming soon",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        root.addView(
            launch,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 58
            )
        )

        val note = TextView(this).apply {
            text = "GMS Virtual Engine • Version 1.0\\n\\nThis is the initial native Android foundation. Virtualization modules will be added and tested separately."
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(8, 32, 8, 0)
        }
        root.addView(note)

        setContentView(root)
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }
    }
}
