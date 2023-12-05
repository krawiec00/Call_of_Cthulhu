package com.app.callofcthulhu

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.app.callofcthulhu.databinding.ActivityWeaponsListBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class WeaponsList : AppCompatActivity() {


    private lateinit var listView: ListView
    private lateinit var firestore: FirebaseFirestore
    private val weaponsList = mutableListOf<String>() // Lista broni

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weapons_list)

        listView = findViewById(R.id.weapon_list)

        // Inicjalizacja bazy danych Firestore
        firestore = FirebaseFirestore.getInstance()

        // Pobieranie broni z kolekcji "weapons"
        fetchWeapons()
    }

    private fun fetchWeapons() {
        // Pobranie danych z kolekcji "weapons"
        firestore.collection("weapons")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val weaponName = document.getString("nazwa") // Zakładając, że atrybut z nazwą broni to "name"
                    weaponName?.let {
                        weaponsList.add(it)
                    }
                }

                // Utworzenie adaptera i wyświetlenie danych w ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, weaponsList)
                listView.adapter = adapter


                // Obsługa kliknięcia elementu listy
                listView.setOnItemClickListener { parent, _, position, _ ->
                    val selectedWeapon = parent.getItemAtPosition(position) as String

                    // Pobierz dane broni z bazy danych na podstawie jej nazwy
                    firestore.collection("weapons")
                        .whereEqualTo("nazwa", selectedWeapon)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val document = documents.first()
                                val weaponId = UUID.randomUUID().toString()
                                val weaponNazwa = document.getString("nazwa") ?: ""
                                val weaponObrazenia = document.getString("obrazenia") ?: ""
                                val weaponAtaki = document.getString("ataki") ?: ""
                                val weaponKoszt = document.getString("koszt") ?: ""
                                val weaponPociski = document.getString("pociski") ?: ""
                                val weaponUmiejetnosc = document.getString("umiejetnosc") ?: ""
                                val weaponZasieg = document.getString("zasieg") ?: ""
                                val weaponZawodnosc = document.getString("zawodnosc") ?: ""

                                // Przekazanie danych do nowego Activity za pomocą Intentu
                                val intent = Intent(this, WeaponsDetailsActivity::class.java)
                                intent.putExtra("nazwa", weaponNazwa)
                                intent.putExtra("obrazenia", weaponObrazenia)
                                intent.putExtra("ataki", weaponAtaki)
                                intent.putExtra("koszt", weaponKoszt)
                                intent.putExtra("pociski", weaponPociski)
                                intent.putExtra("umiejetnosc", weaponUmiejetnosc)
                                intent.putExtra("zasieg", weaponZasieg)
                                intent.putExtra("zawodnosc", weaponZawodnosc)

                                intent.putExtra("id", weaponId)
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