package com.app.callofcthulhu.view.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.R
import com.app.callofcthulhu.model.data.Note
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class NotesAdapter(options: FirestoreRecyclerOptions<Note>, var context: Context) :
    FirestoreRecyclerAdapter<Note, NotesAdapter.NoteViewHolder>(options) {

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, note: Note) {
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        val timestamp = note.timestamp as? Timestamp
        val date = timestamp?.toDate() // Konwersja na obiekt Date
        val formattedDate = date?.let {
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
        }
        holder.dateTextView.text = formattedDate

        holder.itemView.setOnClickListener {
            val intent = Intent(context, NoteDetailsActivity::class.java).apply {
                putExtra("title", note.title)
                putExtra("content", note.content)
                val noteId = snapshots.getSnapshot(position).id
                putExtra("noteId", noteId)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_note_item, parent, false)
        return NoteViewHolder(view)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.note_title_text_view)
        val contentTextView: TextView = itemView.findViewById(R.id.note_content_text_view)
        val dateTextView: TextView = itemView.findViewById(R.id.note_date_text_view)

    }
}
