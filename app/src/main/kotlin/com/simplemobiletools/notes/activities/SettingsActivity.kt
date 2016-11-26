package com.simplemobiletools.notes.activities

import android.os.Bundle
import android.support.v4.app.TaskStackBuilder
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.simplemobiletools.notes.R
import com.simplemobiletools.notes.databases.DBHelper
import com.simplemobiletools.notes.extensions.updateWidget
import com.simplemobiletools.notes.models.Note
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : SimpleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupDarkTheme()
        setupFontSize()
        setupWidgetNote()
    }

    private fun setupDarkTheme() {
        settings_dark_theme.isChecked = config.isDarkTheme
        settings_dark_theme_holder.setOnClickListener {
            settings_dark_theme.toggle()
            config.isDarkTheme = settings_dark_theme.isChecked
            restartActivity()
        }
    }

    private fun setupFontSize() {
        settings_font_size.setSelection(config.fontSize)
        settings_font_size.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                config.fontSize = settings_font_size.selectedItemPosition
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun setupWidgetNote() {
        val notes = DBHelper.newInstance(this).getNotes()
        if (notes.size <= 1) {
            settings_widget_note_holder.visibility = View.GONE
            return
        }

        val adapter = getSpinnerAdapter(notes)
        settings_widget_note.adapter = adapter

        val noteIndex = getNoteIndexWithId(config.widgetNoteId, notes)
        settings_widget_note.setSelection(noteIndex)
        settings_widget_note.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val note = notes[settings_widget_note.selectedItemPosition]
                config.widgetNoteId = note.id
                updateWidget()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun getNoteIndexWithId(id: Int, notes: List<Note>): Int {
        for (i in 0..notes.count() - 1) {
            if (notes[i].id == id) {
                return i
            }
        }
        return 0
    }

    private fun getSpinnerAdapter(notes: List<Note>): ArrayAdapter<String> {
        val titles = notes.map { it.title }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    private fun restartActivity() {
        TaskStackBuilder.create(applicationContext).addNextIntentWithParentStack(intent).startActivities()
    }
}
