package com.android.mynotesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.mynotesapp.adapter.MappingHelper
import com.android.mynotesapp.databinding.ActivityMainBinding
import com.android.mynotesapp.db.NoteHelper
import com.android.mynotesapp.entity.Note
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Notes"

        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        binding.rvList.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
        }

        if (savedInstanceState == null) {
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressbar.visibility = View.VISIBLE
            val noteHelper = NoteHelper.getInstance(applicationContext)
            noteHelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }

            binding.progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
            noteHelper.close()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            when (requestCode) {
                // Akan dipanggil jika request codenya ADD
                NoteAddUpdateActivity.REQUEST_ADD -> if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                    val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note

                    adapter.addItem(note)
                    binding.rvList.smoothScrollToPosition(adapter.itemCount - 1)

                    showSnackbarMessage("Satu item berhasil ditambahkan")
                }
                // Update dan Delete memiliki request code sama akan tetapi result codenya berbeda
                NoteAddUpdateActivity.REQUEST_UPDATE ->
                    when (resultCode) {
                        /*
                        Akan dipanggil jika result codenya  UPDATE
                        Semua data di load kembali dari awal
                        */
                        NoteAddUpdateActivity.RESULT_UPDATE -> {

                            val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)

                            adapter.updateItem(position, note)
                            binding.rvList.smoothScrollToPosition(position)

                            showSnackbarMessage("Satu item berhasil diubah")
                        }
                        /*
                        Akan dipanggil jika result codenya DELETE
                        Delete akan menghapus data dari list berdasarkan dari position
                        */
                        NoteAddUpdateActivity.RESULT_DELETE -> {
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)

                            adapter.removeItem(position)

                            showSnackbarMessage("Satu item berhasil dihapus")
                        }
                    }
            }
        }
    }

    /**
     * Tampilkan snackbar
     *
     * @param message inputan message
     */
    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvList, message, Snackbar.LENGTH_SHORT).show()
    }
}