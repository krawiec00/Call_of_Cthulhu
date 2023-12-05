package com.app.callofcthulhu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID




class SpellsList : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var firestore: FirebaseFirestore
    private val spellsList = mutableListOf<String>() // Lista broni


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spells_list)

        listView = findViewById(R.id.spell_list)

        // Inicjalizacja bazy danych Firestore
        firestore = FirebaseFirestore.getInstance()

        // Pobieranie broni z kolekcji "weapons"
        fetchSpells()
    }

    private fun fetchSpells() {
        // Pobranie danych z kolekcji "weapons"
        firestore.collection("spells")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val weaponName = document.getString("nazwa") // Zakładając, że atrybut z nazwą broni to "name"
                    weaponName?.let {
                        spellsList.add(it)
                    }
                }

                // Utworzenie adaptera i wyświetlenie danych w ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, spellsList)
                listView.adapter = adapter


                // Obsługa kliknięcia elementu listy
                listView.setOnItemClickListener { parent, _, position, _ ->
                    val selectedSpell = parent.getItemAtPosition(position) as String

                    // Pobierz dane broni z bazy danych na podstawie jej nazwy
                    firestore.collection("spells")
                        .whereEqualTo("nazwa", selectedSpell)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val document = documents.first()
                                val spellId = UUID.randomUUID().toString()
                                val spellNazwa = document.getString("nazwa") ?: ""
                                val spellCzas = document.getString("czas") ?: ""
                                val spellKoszt = document.getString("koszt") ?: ""
                                val spellOpis = document.getString("opis") ?: ""


                                // Przekazanie danych do nowego Activity za pomocą Intentu
                                val intent = Intent(this, SpellsDetailsActivity::class.java)
                                intent.putExtra("nazwa", spellNazwa)
                                intent.putExtra("czas", spellCzas)
                                intent.putExtra("koszt", spellKoszt)
                                intent.putExtra("opis", spellOpis)

                                intent.putExtra("id", spellId)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Obsługa błędu pobierania danych z Firestore
                        }
                }
            }
    }


    }
