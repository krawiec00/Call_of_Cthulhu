package com.app.callofcthulhu.view.spells

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.app.callofcthulhu.view.card.CardDetailsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.utils.Utility
import com.app.callofcthulhu.model.data.Spell
import com.google.firebase.firestore.DocumentReference

class SpellsDetailsActivity : AppCompatActivity() {


    private var spellNazwa: String? = null
    private var spellCzas: String? = null
    private var spellKoszt: String? = null
    private var spellOpis: String? = null



    private lateinit var nazwaTextView: TextView
    private lateinit var spellId: String
    private lateinit var czasTextView: TextView
    private lateinit var kosztTextView: TextView
    private lateinit var opisTextView: TextView


    private var added: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spells_details)


        spellNazwa = intent.getStringExtra("nazwa") ?: ""
        spellCzas = intent.getStringExtra("czas") ?: ""
        spellKoszt = intent.getStringExtra("koszt") ?: ""
        spellOpis = intent.getStringExtra("opis") ?: ""

        spellId = intent.getStringExtra("id") ?: ""

        added = intent.getBooleanExtra("added", false)

        nazwaTextView = findViewById(R.id.spell_nazwa)
        czasTextView = findViewById(R.id.spell_czas)
        opisTextView = findViewById(R.id.spell_opis)
        kosztTextView = findViewById(R.id.spell_koszt)



        val addToCardButton: Button = findViewById(R.id.add_spell_to_card_button)
        val deleteWeaponButton: Button = findViewById(R.id.delete_spell_button)

        if(added){
            addToCardButton.visibility = View.GONE
            deleteWeaponButton.visibility = View.VISIBLE
        }

        nazwaTextView.text = spellNazwa
        czasTextView.text = spellCzas
        opisTextView.text = spellOpis
        kosztTextView.text = spellKoszt


        addToCardButton.setOnClickListener {
            saveSpell()
        }

        deleteWeaponButton.setOnClickListener(){
            deleteSpellFromFirebase()
        }

    }

    fun saveSpell(){
        val spell = Spell().apply {
            nazwa = nazwaTextView.text.toString()
            czas = czasTextView.text.toString()
            opis = opisTextView.text.toString()
            koszt = kosztTextView.text.toString()

            id = CardDetailsActivity.docId
        }
        saveSpellToFireBase(spell)
    }

    fun saveSpellToFireBase(spell: Spell){
        val documentReference: DocumentReference = if (spellId.isNotEmpty()) {
            // Update the note
            Utility.getCollectionReferenceForSpells().document(spellId)
        } else {
            // Create new note
            Utility.getCollectionReferenceForSpells().document()
        }

        documentReference.set(spell).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Dodano zaklęcie", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(baseContext, "Blad przy dodawaniu zaklęcia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteSpellFromFirebase() {
        val documentReference: DocumentReference = Utility.getCollectionReferenceForSpells().document(spellId)
        documentReference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //note is deleted
                Toast.makeText(baseContext, "Zaklęcie usunięte", Toast.LENGTH_SHORT).show()

                finish()
            } else {
                Toast.makeText(baseContext, "Błąd prrzy usuwaniu zaklęcia", Toast.LENGTH_SHORT).show()

            }
        }
    }


}