package com.example.notesroom.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.notesroom.R
import com.example.notesroom.databinding.ActivityAddNotesBinding
import com.example.notesroom.models.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.logging.SimpleFormatter

class AddNotes : AppCompatActivity() {

    private lateinit var binding: ActivityAddNotesBinding
    private lateinit var note: Note
    private lateinit var old_note: Note
    var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try{
            old_note = intent.getSerializableExtra("current_note") as Note
            binding.addTitle.setText(old_note.title)
            binding.addBody.setText(old_note.body)
            isUpdate = true
        }catch (e: Exception){
            e.printStackTrace()
        }
        binding.doneBtn.setOnClickListener {
            val title = binding.addTitle.text.toString()
            val body = binding.addBody.text.toString()

            if (title.isNotEmpty() || body.isNotEmpty()){
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")

                if (isUpdate){
                    note = Note(old_note.id, title, body, formatter.format(Date()))
                }else{
                    note = Note(null, title, body, formatter.format(Date()))
                }
                val intent = Intent()
                intent.putExtra("note", note)
                setResult(Activity.RESULT_OK, intent)
                val keyboardManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboardManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
                finish()
            }else{
                Toast.makeText(this@AddNotes, "Please Enter Some Data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
        binding.backBtn.setOnClickListener {
            val keyboardManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboardManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
            onBackPressed()
        }
    }
}