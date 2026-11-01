package com.gms.virtualengine

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.text.Editable
import android.text.TextWatcher

class MainActivity : Activity() {

    private val bg = Color.rgb(7, 13, 30)
    private val panel = Color.rgb(17, 29, 57)
    private val blue = Color.rgb(40, 130, 255)
    private val cyan = Color.rgb(0, 210, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(157, 174, 205)

    private lateinit var appList: LinearLayout
    private lateinit var countText: TextView
    private lateinit var searchBox: EditText
    private var searchQuery = ""

    private val prefs by lazy {
        getSharedPreferences("gms_managed_apps", MODE_PRIVATE)
    }

    data class AppInfo(
        val label: String,
        val packageName: String
    )

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(18), dp(16), dp(18), dp(12))
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // TOP BRAND
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = TextView(this).apply {
            text = "GMS VIRTUAL ENGINE"
            textSize = 21f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.04f
        }

        val version = TextView(this).apply {
            text = "  V1.1"
            textSize = 10f
            setTextColor(cyan)
            setPadding(dp(8), dp(6), dp(8), dp(6))
            background = shape(panel, 10)
        }

        top.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(version)
        content.addView(top)

        val welcome = text(
            "Your apps. Your space. Your control.",
            13f, muted
        )
        welcome.setPadding(0, dp(7), 0, dp(22))
        content.addView(welcome)

        // HERO CARD
        val hero = card()
        hero.orientation = LinearLayout.VERTICAL
        hero.setPadding(dp(19), dp(19), dp(19), dp(19))

        hero.addView(text("ENGINE OVERVIEW", 11f, muted))

        val status = text("●  APP MANAGER READY", 19f, cyan)
        status.typeface = Typeface.DEFAULT_BOLD
        status.setPadding(0, dp(12), 0, dp(7))
        hero.addView(status)

        hero.addView(text(
            "Manage and launch your selected apps from one place.",
            13f, muted
        ))

        content.addView(hero, marginBottom(20))

        // STATISTICS
        val stats = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val statCard = card()
        statCard.orientation = LinearLayout.VERTICAL
        statCard.setPadding(dp(16), dp(15), dp(16), dp(15))

        statCard.addView(text("MANAGED APPS", 10f, muted))

        countText = text("0", 29f, white)
        countText.typeface = Typeface.DEFAULT_BOLD
        countText.setPadding(0, dp(6), 0, 0)
        statCard.addView(countText)

        stats.addView(statCard, LinearLayout.LayoutParams(0, -2, 1f))
        content.addView(stats, marginBottom(22))

        // SECTION TITLE
        val section = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val sectionTitle = text("MY VIRTUAL SPACE", 16f, white)
        sectionTitle.typeface = Typeface.DEFAULT_BOLD
        section.addView(sectionTitle, LinearLayout.LayoutParams(0, -2, 1f))

        section.addView(text("LOCAL", 10f, cyan))
        content.addView(section, marginBottom(12))

        // SEARCH
        searchBox = EditText(this).apply {
            hint = "Search managed apps..."
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 14f
            setSingleLine(true)
            setPadding(dp(15), dp(12), dp(15), dp(12))
            background = shape(panel, 14)
        }

        content.addView(searchBox, marginBottom(14))

        appList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        content.addView(appList, marginBottom(16))

        // ADD APP BUTTON
        val addButton = button("+   ADD AUTHORIZED APP")
        addButton.setOnClickListener { showAppPicker() }

        content.addView(addButton, marginBottom(12))

        val note = text(
            "Apps are saved locally on this device. " +
            "This manager launches installed apps; " +
            "it does not clone or isolate them.",
            11f, muted
        )
        note.gravity = Gravity.CENTER
        note.setPadding(dp(8), dp(12), dp(8), dp(24))
        content.addView(note)

        scroll.addView(content)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        val footer = text(
            "GMS ENGINE  •  BUILT FOR ANDROID",
            10f, muted
        )
        footer.gravity = Gravity.CENTER
        footer.setPadding(0, dp(12), 0, dp(5))
        root.addView(footer)

        setContentView(root)

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}
            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                searchQuery = s?.toString()?.trim() ?: ""
                refreshApps()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        refreshApps()
    }

    private fun text(
        value: String,
        size: Float,
        color: Int
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
        }
    }

    private fun card(): LinearLayout {
        return LinearLayout(this).apply {
            setBackgroundGradient(
                intArrayOf(
                    Color.rgb(20, 37, 72),
                    Color.rgb(13, 24, 49)
                ), 20
            )
        }
    }

    private fun shape(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), Color.rgb(37, 65, 105))
        }
    }

    private fun button(label: String): TextView {
        return TextView(this).apply {
            text = label
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(white)
            setPadding(dp(12), dp(17), dp(12), dp(17))
            setBackgroundGradient(
                intArrayOf(
                    Color.rgb(37, 120, 255),
                    Color.rgb(0, 174, 235)
                ), 15
            )
            isClickable = true
            isFocusable = true
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN ->
                        v.animate().scaleX(0.97f).scaleY(0.97f)
                            .setDuration(90).start()
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL ->
                        v.animate().scaleX(1f).scaleY(1f)
                            .setDuration(110).start()
                }
                false
            }
        }
    }

    private fun View.setBackgroundGradient(
        colors: IntArray,
        radius: Int
    ) {
        background = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            colors
        ).apply {
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), Color.rgb(37, 65, 105))
        }
    }

    private fun marginBottom(value: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = dp(value)
        }
    }

    private fun installedApps(): List<AppInfo> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        return packageManager.queryIntentActivities(intent, 0)
            .mapNotNull {
                val packageName = it.activityInfo?.packageName ?: return@mapNotNull null
                if (packageName == packageNameOfSelf()) return@mapNotNull null

                AppInfo(
                    it.loadLabel(packageManager).toString(),
                    packageName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    private fun packageNameOfSelf() = packageName

    private fun managedPackages(): MutableSet<String> {
        return prefs.getStringSet("packages", emptySet())
            ?.toMutableSet() ?: mutableSetOf()
    }

    private fun showAppPicker() {
        val allApps = installedApps()
        val selected = managedPackages()

        val available = allApps.filter {
            !selected.contains(it.packageName)
        }

        if (available.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("No more apps")
                .setMessage("All available launcher apps are already managed, or no compatible apps were found.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val labels = available.map { it.label }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Choose an installed app")
            .setItems(labels) { _, which ->
                selected.add(available[which].packageName)
                prefs.edit()
                    .putStringSet("packages", selected)
                    .apply()
                refreshApps()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun refreshApps() {
        if (!::appList.isInitialized) return

        appList.removeAllViews()

        val selected = managedPackages()
        val all = installedApps().filter {
            selected.contains(it.packageName) &&
            it.label.contains(searchQuery, ignoreCase = true)
        }

        countText.text = selected.size.toString()

        if (all.isEmpty()) {
            val empty = card()
            empty.orientation = LinearLayout.VERTICAL
            empty.gravity = Gravity.CENTER
            empty.setPadding(dp(20), dp(25), dp(20), dp(25))

            empty.addView(text("✦", 30f, cyan))
            val message = text(
                if (selected.isEmpty())
                    "Your space is waiting"
                else
                    "No matching apps found",
                15f, white
            )
            message.typeface = Typeface.DEFAULT_BOLD
            message.gravity = Gravity.CENTER
            message.setPadding(0, dp(8), 0, dp(5))
            empty.addView(message)

            val hint = text(
                "Tap ADD AUTHORIZED APP to get started.",
                12f, muted
            )
            hint.gravity = Gravity.CENTER
            empty.addView(hint)

            appList.addView(empty, marginBottom(12))
            return
        }

        all.forEach { app ->
            appList.addView(createAppRow(app), marginBottom(10))
        }
    }

    private fun createAppRow(app: AppInfo): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = shape(panel, 16)
        }

        val icon = ImageView(this).apply {
            try {
                setImageDrawable(packageManager.getApplicationIcon(app.packageName))
            } catch (_: Exception) {
                setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }

        row.addView(icon, LinearLayout.LayoutParams(dp(43), dp(43)))

        val names = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(11), 0, dp(5), 0)
        }

        val name = text(app.label, 14f, white)
        name.typeface = Typeface.DEFAULT_BOLD
        names.addView(name)

        val pkg = text(app.packageName, 9f, muted)
        pkg.maxLines = 1
        pkg.maxWidth = dp(145)
        names.addView(pkg)

        row.addView(names, LinearLayout.LayoutParams(0, -2, 1f))

        val launch = TextView(this).apply {
            text = "OPEN"
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(cyan)
            setPadding(dp(11), dp(10), dp(11), dp(10))
            background = shape(Color.rgb(12, 45, 80), 10)
            setOnClickListener {
                val launchIntent =
                    packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    try {
                        startActivity(launchIntent)
                    } catch (_: Exception) {
                        Toast.makeText(
                            this@MainActivity,
                            "Unable to launch this app",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        row.addView(launch)

        row.setOnLongClickListener {
            AlertDialog.Builder(this)
                .setTitle(app.label)
                .setMessage("Remove this app from your GMS managed list? The original app will remain installed.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Remove") { _, _ ->
                    val packages = managedPackages()
                    packages.remove(app.packageName)
                    prefs.edit()
                        .putStringSet("packages", packages)
                        .apply()
                    refreshApps()
                }
                .show()
            true
        }

        return row
    }
}
