package com.gms.virtualengine

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MainActivity : Activity() {

    private val bg = Color.rgb(7, 13, 30)
    private val panel = Color.rgb(18, 31, 59)
    private val blue = Color.rgb(39, 126, 255)
    private val cyan = Color.rgb(0, 210, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(160, 177, 207)

    private val prefs by lazy {
        getSharedPreferences("gms_plugin_manager", MODE_PRIVATE)
    }

    private lateinit var content: LinearLayout
    private lateinit var statusText: TextView

    private var selectedGame = -1
    private var pendingGame = -1

    private val games = JSONArray()

    private val picker = 1001

    private fun dp(v: Int) =
        (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadGames()

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(18), dp(20), dp(18), dp(10))
        }

        val header = TextView(this).apply {
            text = "GMS VIRTUAL ENGINE"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
        }
        root.addView(header)

        val subtitle = TextView(this).apply {
            text = "NATIVE PLUGIN MANAGEMENT SYSTEM"
            textSize = 10f
            letterSpacing = 0.12f
            setTextColor(cyan)
            setPadding(0, dp(7), 0, dp(18))
        }
        root.addView(subtitle)

        statusText = TextView(this).apply {
            text = "●  PLUGIN MANAGER READY"
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(cyan)
            setPadding(dp(14), dp(15), dp(14), dp(15))
            background = shape(panel, 15)
        }
        root.addView(statusText)

        val scroll = ScrollView(this)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(16), 0, dp(20))
        }

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val footer = TextView(this).apply {
            text = "GMS ENGINE  •  NATIVE PLUGIN SYSTEM"
            textSize = 10f
            gravity = Gravity.CENTER
            setTextColor(muted)
            setPadding(0, dp(12), 0, dp(8))
        }
        root.addView(footer)

        setContentView(root)
        showDashboard()
    }

    private fun shape(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), Color.rgb(42, 68, 108))
        }
    }

    private fun gradient(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(blue, Color.rgb(0, 175, 235))
        ).apply {
            cornerRadius = dp(15).toFloat()
        }
    }

    private fun label(value: String, size: Float = 14f,
                      color: Int = white): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
        }
    }

    private fun button(value: String, action: () -> Unit): TextView {
        return TextView(this).apply {
            text = value
            textSize = 14f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
            setPadding(dp(12), dp(16), dp(12), dp(16))
            background = gradient()
            isClickable = true
            setOnClickListener {
                animate().scaleX(0.97f).scaleY(0.97f)
                    .setDuration(80).withEndAction {
                        animate().scaleX(1f).scaleY(1f)
                            .setDuration(100).start()
                    }.start()
                action()
            }
        }
    }

    private fun addGap(parent: LinearLayout, height: Int = 12) {
        parent.addView(
            android.view.View(this),
            LinearLayout.LayoutParams(1, dp(height))
        )
    }

    private fun card(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(17), dp(16), dp(17))
            background = shape(panel, 17)
        }
    }

    private fun loadGames() {
        val raw = prefs.getString("games", "[]") ?: "[]"
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) games.put(array.getJSONObject(i))
        } catch (_: Exception) {}
    }

    private fun saveGames() {
        prefs.edit().putString("games", games.toString()).apply()
    }

    private fun showDashboard() {
        content.removeAllViews()

        val title = label("PLUGIN DASHBOARD", 17f)
        title.typeface = Typeface.DEFAULT_BOLD
        content.addView(title)
        addGap(content, 13)

        val overview = card()
        overview.addView(label("REGISTERED GAME PROFILES", 10f, muted))
        addGap(overview, 8)
        overview.addView(label(games.length().toString(), 31f))
        addGap(overview, 4)
        overview.addView(label("Stored locally on this device", 11f, muted))
        content.addView(overview)
        addGap(content, 16)

        content.addView(button("+   ADD NEW GAME PROFILE") {
            showAddGame()
        })
        addGap(content, 12)

        content.addView(button("▣   NATIVE LIBRARY MANAGER") {
            showLibraryInfo()
        })
        addGap(content, 22)

        val section = label("YOUR GAME PROFILES", 15f)
        section.typeface = Typeface.DEFAULT_BOLD
        content.addView(section)
        addGap(content, 12)

        if (games.length() == 0) {
            val empty = card()
            empty.gravity = Gravity.CENTER
            empty.addView(label("✦", 30f, cyan))
            addGap(empty, 8)
            empty.addView(label("No game profiles yet", 15f))
            addGap(empty, 5)
            empty.addView(label("Add your first game profile above.", 11f, muted))
            content.addView(empty)
        }

        for (i in 0 until games.length()) {
            val game = games.getJSONObject(i)
            val item = card()

            val name = label(game.optString("name"), 16f)
            name.typeface = Typeface.DEFAULT_BOLD
            item.addView(name)
            addGap(item, 6)
            item.addView(label(game.optString("package"), 11f, muted))
            addGap(item, 10)

            val info = label(
                "Architecture: ${game.optString("abi", "Not checked")}",
                11f, cyan
            )
            item.addView(info)
            addGap(item, 12)

            item.addView(button("VIEW COMPATIBILITY REPORT") {
                showReport(i)
            })
            addGap(item, 8)

            item.addView(button("REMOVE PROFILE") {
                AlertDialog.Builder(this)
                    .setTitle("Remove game profile?")
                    .setMessage("This removes only the saved profile, not the installed game.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Remove") { _, _ ->
                        games.remove(i)
                        saveGames()
                        showDashboard()
                    }.show()
            })

            content.addView(item)
            addGap(content, 12)
        }
    }

    private fun showAddGame() {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(10), dp(20), 0)
        }

        val name = EditText(this).apply {
            hint = "Game / App Name"
            setTextColor(white)
            setHintTextColor(muted)
        }

        val pkg = EditText(this).apply {
            hint = "Package Name (example: com.example.game)"
            setTextColor(white)
            setHintTextColor(muted)
        }

        box.addView(name)
        box.addView(pkg)

        AlertDialog.Builder(this)
            .setTitle("Create Game Profile")
            .setView(box)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Create") { _, _ ->
                val n = name.text.toString().trim()
                val p = pkg.text.toString().trim()

                if (n.isBlank() || !p.matches(
                    Regex("[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z0-9_]+)+")
                )) {
                    Toast.makeText(
                        this, "Enter a valid name and package ID",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }

                val obj = JSONObject()
                obj.put("name", n)
                obj.put("package", p)
                obj.put("abi", "Not checked")
                obj.put("library", "")
                games.put(obj)
                saveGames()
                showDashboard()
            }.show()
    }

    private fun showReport(index: Int) {
        val game = games.getJSONObject(index)
        val pkg = game.optString("package")
        val abi = android.os.Build.SUPPORTED_ABIS.joinToString()

        val installed = try {
            packageManager.getApplicationInfo(pkg, 0)
            true
        } catch (_: Exception) {
            false
        }

        val report = """
            GAME: ${game.optString("name")}

            PACKAGE: $pkg

            DEVICE ABI:
            $abi

            INSTALLED:
            ${if (installed) "YES" else "NOT FOUND"}

            NATIVE LIBRARY:
            ${game.optString("library", "Not imported")}

            REQUIRED CHECKS:

            1. Confirm game package and ownership.
            2. Match the library ABI with the target app.
            3. Check Android API compatibility.
            4. Provide an authorized Plugin API in your own game.
            5. Implement plugin initialization and error handling.
            6. Test on a separate test build.

            STATUS:
            ${if (installed) "Profile found; compatibility needs testing."
              else "Install the app or verify the package name."}

            NOTE:
            Importing a LIB does not inject it into another app.
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Compatibility Report")
            .setMessage(report)
            .setPositiveButton("Import LIB") { _, _ ->
                pendingGame = index
                chooseLibrary()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun chooseLibrary() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, picker)
    }

    @Deprecated("Use Activity Result API in future update")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != picker || resultCode != RESULT_OK) return

        val uri: Uri = data?.data ?: return
        val filename = uri.lastPathSegment ?: "native-library.so"

        if (!filename.endsWith(".so", true)) {
            Toast.makeText(this, "Please select a .so library", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val dir = File(filesDir, "native_libraries")
            dir.mkdirs()

            val safeName = "library_${System.currentTimeMillis()}.so"
            val output = File(dir, safeName)

            contentResolver.openInputStream(uri)?.use { input ->
                output.outputStream().use { out -> input.copyTo(out) }
            } ?: throw Exception("Cannot read selected file")

            if (pendingGame in 0 until games.length()) {
                val game = games.getJSONObject(pendingGame)
                game.put("library", safeName)
                game.put("abi", android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown")
                saveGames()
            }

            Toast.makeText(
                this,
                "LIB imported for compatibility review",
                Toast.LENGTH_LONG
            ).show()

            showDashboard()

        } catch (e: Exception) {
            Toast.makeText(
                this, "Import failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showLibraryInfo() {
        AlertDialog.Builder(this)
            .setTitle("Native Library Manager")
            .setMessage(
                "Import and store your authorized .so files.\n\n" +
                "The manager records the library filename and checks " +
                "the device ABI. It does not execute, inject, or load " +
                "arbitrary libraries into other apps.\n\n" +
                "For your own game, add a documented Plugin API " +
                "and link the library in that game's source code."
            )
            .setPositiveButton("OK", null)
            .show()
    }
}
