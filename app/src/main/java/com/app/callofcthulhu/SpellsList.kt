package com.app.callofcthulhu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.util.UUID


class SpellsList : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spells_list)

        val spellList = findViewById<ListView>(R.id.spell_list)
        val spellNames = mutableListOf<String>()

        try {
            val spellsJson = loadJSONFromAsset() // Load JSON data from file
            val spellsArray = spellsJson.getJSONArray("spells")

            for (i in 0 until spellsArray.length()) {
                val spellObject = spellsArray.getJSONObject(i)
                val spellName = spellObject.getString("nazwa")
                spellNames.add(spellName)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val adapter = ArrayAdapter(this, R.layout.custom_list_item, R.id.item_name,  spellNames)
        spellList.adapter = adapter

        spellList.setOnItemClickListener { _, _, position, _ ->
            val clickedSpellName = spellNames[position] // Nazwa klikniętego zaklęcia

            try {
                val spellsJson = loadJSONFromAsset() // Load JSON data from file
                val spellsArray = spellsJson.getJSONArray("spells")

                for (i in 0 until spellsArray.length()) {
                    val spellObject = spellsArray.getJSONObject(i)
                    val spellName = spellObject.getString("nazwa")

                    if (spellName == clickedSpellName) {
                        // Pobierz pozostałe parametry zaklęcia
                        val spellId = UUID.randomUUID().toString()
                        val spellNazwa = spellObject.getString("nazwa") ?: ""
                        val spellCzas = spellObject.getString("czas") ?: ""
                        val spellKoszt = spellObject.getString("koszt") ?: ""
                        val spellOpis = spellObject.getString("opis") ?: ""

                        // Przekazanie parametrów do nowej aktywności
                        val intent = Intent(this, SpellsDetailsActivity::class.java)
                        intent.putExtra("nazwa", spellNazwa)
                        intent.putExtra("czas", spellCzas)
                        intent.putExtra("koszt", spellKoszt)
                        intent.putExtra("opis", spellOpis)

                        intent.putExtra("id", spellId)

                        startActivity(intent) // Uruchomienie nowej aktywności
                        break
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun loadJSONFromAsset(): JSONObject {
        val json: String?
        try {
            val inputStream = assets.open("spells.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return JSONObject()
        }
        return JSONObject(json)
    }


    }
