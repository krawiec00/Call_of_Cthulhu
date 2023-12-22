package com.app.callofcthulhu.fragments

import com.app.callofcthulhu.SharedViewModel
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.Note
import com.app.callofcthulhu.NoteDetailsActivity
import com.app.callofcthulhu.NotesAdapter
import com.app.callofcthulhu.R
import com.app.callofcthulhu.Utility
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForCards
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query


class StoryFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var opisEditText: EditText
    private lateinit var ideologiaEditText: EditText
    private lateinit var osobyEditText: EditText
    private lateinit var miejscaEditText: EditText
    private lateinit var rzeczyEditText: EditText
    private lateinit var przymiotyEditText: EditText
    private lateinit var urazyEditText: EditText
    private lateinit var fobieEditText: EditText
    private lateinit var ksiegiEditText: EditText
    private lateinit var istotyEditText: EditText
    private lateinit var saveNoteBtn: ImageButton
    private lateinit var notatkiLayout: LinearLayout
    private lateinit var notatkiRecyclerView: RecyclerView


    lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NotesAdapter

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_story, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        opisEditText = view.findViewById(R.id.card_opis)
        ideologiaEditText = view.findViewById(R.id.card_ideologia)
        osobyEditText = view.findViewById(R.id.card_wosoby)
        miejscaEditText = view.findViewById(R.id.card_miejsca)
        rzeczyEditText = view.findViewById(R.id.card_rzeczyo)
        przymiotyEditText = view.findViewById(R.id.card_przymioty)
        urazyEditText = view.findViewById(R.id.card_urazy)
        fobieEditText = view.findViewById(R.id.card_fobie)
        ksiegiEditText = view.findViewById(R.id.card_ksiegi)
        istotyEditText = view.findViewById(R.id.card_istoty)
        saveNoteBtn = view.findViewById(R.id.save_note_btn)

        recyclerView = view.findViewById(R.id.recycler_view)

        notatkiLayout = view.findViewById(R.id.notatki)
        notatkiRecyclerView = view.findViewById(R.id.recycler_view)

        attachTextWatcher(opisEditText, "opis")
        attachTextWatcher(ideologiaEditText, "ideologia")
        attachTextWatcher(osobyEditText, "osoby")
        attachTextWatcher(miejscaEditText, "miejsca")
        attachTextWatcher(rzeczyEditText, "rzeczy")
        attachTextWatcher(przymiotyEditText, "przymioty")
        attachTextWatcher(urazyEditText, "urazy")
        attachTextWatcher(fobieEditText, "fobie")
        attachTextWatcher(ksiegiEditText, "ksiegi")
        attachTextWatcher(istotyEditText, "istoty")

        saveNoteBtn.setOnClickListener {
            val intent = Intent(requireActivity(), NoteDetailsActivity::class.java)
            startActivity(intent)
        }


        val id = CardDetailsActivity.docId
        if (id.isNotEmpty()) {
            getCollectionReferenceForCards().document(id).get().addOnSuccessListener { document ->
                document?.let {
                    if (it.exists()) {
                        val fields = mapOf(
                            "opis" to opisEditText,
                            "ideologia" to ideologiaEditText,
                            "osoby" to osobyEditText,
                            "miejsca" to miejscaEditText,
                            "rzeczy" to rzeczyEditText,
                            "przymioty" to przymiotyEditText,
                            "urazy" to urazyEditText,
                            "fobie" to fobieEditText,
                            "ksiegi" to ksiegiEditText,
                            "istoty" to istotyEditText
                        )

                        fields.forEach { (key, editText) ->
                            editText.setText(document.getString(key))
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                // Obsługa błędu pobierania danych z Firestore
            }
            notatkiLayout.visibility = View.VISIBLE
            notatkiRecyclerView.visibility = View.VISIBLE
        }

        setupRecyclerView()

        return view
    }

    fun attachTextWatcher(editText: EditText, fieldName: String) {
        editText.addTextChangedListener { editable ->
            sharedViewModel.updateCardField(fieldName, editable.toString())
        }
    }

    fun setupRecyclerView() {
        val userId = CardDetailsActivity.docId
        val query: Query = Utility.getCollectionReferenceForNotes()
            .orderBy("title", Query.Direction.DESCENDING)
            .whereEqualTo("id", userId) // Dodaj warunek na pole 'id'

        val options: FirestoreRecyclerOptions<Note> = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java).build()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NotesAdapter(options, requireContext())
        recyclerView.adapter = noteAdapter
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        noteAdapter.notifyDataSetChanged()
    }

}