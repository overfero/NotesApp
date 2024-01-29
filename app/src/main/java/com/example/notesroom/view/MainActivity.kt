package com.example.notesroom.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesroom.Adapter.NotesAdapter
import com.example.notesroom.R
import com.example.notesroom.database.NoteDatabase
import com.example.notesroom.databinding.ActivityMainBinding
import com.example.notesroom.models.Note
import com.example.notesroom.models.NotesViewModel

class MainActivity : AppCompatActivity(), NotesAdapter.NotesClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    private lateinit var viewModel: NotesViewModel
    private lateinit var selectedNote: Note
    lateinit var adapter: NotesAdapter

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
            val note = result.data?.getSerializableExtra("note") as? Note
            if (note != null){viewModel.updateNote(note)}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI{}

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(NotesViewModel::class.java)
        viewModel.allNotes.observe(this){list ->
            list?.let{ adapter.updateList(list) }
        }
        database = NoteDatabase.getDatabase(this)
    }

    private fun initUI(function: () -> Unit) {
        binding.listNotes.setHasFixedSize(true)
        binding.listNotes.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this, this)
        binding.listNotes.adapter = adapter

        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode == Activity.RESULT_OK){
                val note = result.data?.getSerializableExtra("note") as? Note
                if (note != null){
                    viewModel.insertNote(note)
                }
            }
        }
        binding.addBtn.setOnClickListener {
            val intent = Intent(this@MainActivity,AddNotes::class.java)
            getContent.launch(intent)
        }
        binding.search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null){
                    adapter.filterList(newText)
                }
                return true
            }

        })
    }

    override fun onItemClicked(notes: Note) {
        val intent = Intent(this@MainActivity,AddNotes::class.java)
        intent.putExtra("current_note",notes)
        updateNote.launch(intent)
    }

    override fun onLongItemClicked(notes: Note, cardView: CardView) {
        selectedNote = notes
        popUpDisplay(cardView)
    }
    private fun popUpDisplay(cardView: CardView){
        val popup = PopupMenu(this,cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.popup_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_note){
            viewModel.deleteNote(selectedNote)
            return true
        }
        return false
    }
}