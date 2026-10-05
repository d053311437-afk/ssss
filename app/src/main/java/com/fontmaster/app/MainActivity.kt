package com.fontmaster.app

import android.graphics.Typeface
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.provider.Settings
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fontmaster.app.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnImportFont.text = "ספריית גופנים מובנית"
        binding.btnImportFont.setOnClickListener { Toast.makeText(this, "בגרסה 2 הספרייה מובנית — ללא ייבוא", Toast.LENGTH_SHORT).show() }
        binding.btnDefaultFont.setOnClickListener {
            binding.previewText.typeface = Typeface.DEFAULT
            binding.selectedFont.text = getString(R.string.default_font)
            Toast.makeText(this, getString(R.string.default_restored), Toast.LENGTH_SHORT).show()
        }
        binding.fontSizeSeek.max = 40
        binding.fontSizeSeek.progress = 12
        binding.fontSizeSeek.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = progress + 12
                binding.previewText.textSize = size.toFloat()
                binding.fontSizeValue.text = "$size sp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun legacyLoadFont(uri: android.net.Uri) {
        try {
            val name = getFileName(uri)
            val ext = if (name.endsWith(".otf", true)) ".otf" else ".ttf"
            val local = File(cacheDir, "fontmaster_selected$ext")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(local).use { output -> input.copyTo(output) }
            }
            binding.previewText.typeface = Typeface.createFromFile(local)
            binding.selectedFont.text = name
            Toast.makeText(this, getString(R.string.font_loaded), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.font_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileName(uri: android.net.Uri): String {
        var result = "Custom Font"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val i = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (i >= 0 && cursor.moveToFirst()) result = cursor.getString(i)
        }
        return result
    }
}
