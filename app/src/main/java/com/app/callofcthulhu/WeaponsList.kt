package com.app.callofcthulhu

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.util.UUID

class WeaponsList : AppCompatActivity() {


//    private lateinit var listView: ListView
//    private lateinit var firestore: FirebaseFirestore
//    private val weaponsList = mutableListOf<String>() // Lista broni

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weapons_list)

        val weaponList = findViewById<ListView>(R.id.weapon_list)
        val weaponNames = mutableListOf<String>()

        try {
            val weaponsJson = loadJSONFromAsset() // Load JSON data from file
            val weaponsArray = weaponsJson.getJSONArray("weapons")

            for (i in 0 until weaponsArray.length()) {
                val weaponObject = weaponsArray.getJSONObject(i)
                val weaponName = weaponObject.getString("nazwa")
                weaponNames.add(weaponName)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, weaponNames)
        weaponList.adapter = adapter

        weaponList.setOnItemClickListener { _, _, position, _ ->
            val clickedWeaponName = weaponNames[position] // Nazwa klikniętej broni

            // Pobierz resztę parametrów broni na podstawie klikniętej nazwy z wczytanego wcześniej pliku JSON
            try {
                val weaponsJson = loadJSONFromAsset() // Load JSON data from file
                val weaponsArray = weaponsJson.getJSONArray("weapons")

                for (i in 0 until weaponsArray.length()) {
                    val weaponObject = weaponsArray.getJSONObject(i)
                    val weaponName = weaponObject.getString("nazwa")

                    if (weaponName == clickedWeaponName) {
                        // Pobierz pozostałe parametry broni


                        val weaponId = UUID.randomUUID().toString()
                        val weaponObrazenia = weaponObject.getString("obrazenia") ?: ""
                        val weaponAtaki = weaponObject.getString("ataki") ?: ""
                        val weaponKoszt = weaponObject.getString("koszt") ?: ""
                        val weaponPociski = weaponObject.getString("pociski") ?: ""
                        val weaponUmiejetnosc = weaponObject.getString("umiejetnosc") ?: ""
                        val weaponZasieg = weaponObject.getString("zasieg") ?: ""
                        val weaponZawodnosc = weaponObject.getString("zawodnosc") ?: ""



                        // Przekazanie parametrów do nowej aktywności
                        val intent = Intent(this, WeaponsDetailsActivity::class.java)
                        intent.putExtra("nazwa", clickedWeaponName)
                        intent.putExtra("obrazenia", weaponObrazenia)
                        intent.putExtra("ataki", weaponAtaki)
                        intent.putExtra("koszt", weaponKoszt)
                        intent.putExtra("pociski", weaponPociski)
                        intent.putExtra("umiejetnosc", weaponUmiejetnosc)
                        intent.putExtra("zasieg", weaponZasieg)
                        intent.putExtra("zawodnosc", weaponZawodnosc)

                        intent.putExtra("id", weaponId)

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
            val inputStream = assets.open("weapons.json")
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