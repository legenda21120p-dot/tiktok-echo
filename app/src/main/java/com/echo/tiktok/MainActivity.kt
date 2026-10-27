package com.echo.tiktok

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    
    private lateinit var switchEnable: SwitchMaterial
    private lateinit var dropdownRegion: AutoCompleteTextView
    private lateinit var tvStatus: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        switchEnable = findViewById(R.id.switchEnable)
        dropdownRegion = findViewById(R.id.dropdownRegion)
        tvStatus = findViewById(R.id.tvStatus)
        
        setupRegionDropdown()
        loadSettings()
        setupListeners()
    }
    
    private fun setupRegionDropdown() {
        val adapter = ArrayAdapter(
            this,
            R.layout.item_dropdown,
            Prefs.AVAILABLE_REGIONS
        )
        dropdownRegion.setAdapter(adapter)
    }
    
    private fun loadSettings() {
        val isEnabled = Prefs.isEnabled(this)
        val region = Prefs.getRegion(this)
        
        switchEnable.isChecked = isEnabled
        dropdownRegion.setText(region, false)
        updateStatus(isEnabled)
    }
    
    private fun setupListeners() {
        switchEnable.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setEnabled(this, isChecked)
            updateStatus(isChecked)
        }
        
        dropdownRegion.setOnItemClickListener { _, _, position, _ ->
            val selectedRegion = Prefs.AVAILABLE_REGIONS[position]
            Prefs.setRegion(this, selectedRegion)
        }
    }
    
    private fun updateStatus(enabled: Boolean) {
        if (enabled) {
            tvStatus.text = getString(R.string.status_active)
            tvStatus.setTextColor(0xFF00FF88.toInt())
        } else {
            tvStatus.text = getString(R.string.status_inactive)
            tvStatus.setTextColor(0xFFFF6B6B.toInt())
        }
    }
}
