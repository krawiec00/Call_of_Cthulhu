package com.app.callofcthulhu.view.weapons


import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.app.callofcthulhu.view.card.CardDetailsActivity.Companion.docId
import com.app.callofcthulhu.R
import com.app.callofcthulhu.model.data.Card
import com.app.callofcthulhu.utils.Utility
import com.app.callofcthulhu.model.data.Weapon
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.google.firebase.firestore.DocumentReference

class WeaponsDetailsActivity : AppCompatActivity() {

    val sharedViewModel = SharedViewModelInstance.instance

    private var weaponNazwa: String? = null
    private var weaponObrazenia: String? = null
    private var weaponAtaki: String? = null
    private var weaponKoszt: String? = null
    private var weaponPociski: String? = null
    private var weaponUmiejetnosc: String? = null
    private var weaponZasieg: String? = null
    private var weaponZawodnosc: String? = null

    private lateinit var nameTextView: TextView
    private lateinit var weaponId: String
    private lateinit var damageTextView: TextView
    private lateinit var atakiTextView: TextView
    private lateinit var kosztTextView: TextView
    private lateinit var pociskiTextView: TextView
    private lateinit var umiejetnoscTextView: TextView
    private lateinit var zasiegTextView: TextView
    private lateinit var zawodnoscTextView: TextView
    private lateinit var skillTextView: TextView
    private lateinit var skillValueTextView: TextView
    private lateinit var skillLayout: LinearLayout

    private var added: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weapons_details)


        nameTextView = findViewById(R.id.weapon_nazwa)
        damageTextView = findViewById(R.id.weapon_obrazenia)
        atakiTextView = findViewById(R.id.weapon_ataki)
        kosztTextView = findViewById(R.id.weapon_koszt)
        pociskiTextView = findViewById(R.id.weapon_pociski)
        umiejetnoscTextView = findViewById(R.id.weapon_umiejetnosci)
        zasiegTextView = findViewById(R.id.weapon_zasieg)
        zawodnoscTextView = findViewById(R.id.weapon_zawodnosc)
        skillTextView = findViewById(R.id.weapon_skill)
        skillValueTextView = findViewById(R.id.weapon_skill_value)
        skillLayout = findViewById(R.id.layout_weapons_fields)

        added = intent.getBooleanExtra("added", false)
        weaponNazwa = intent.getStringExtra("nazwa") ?: ""
        weaponObrazenia = intent.getStringExtra("obrazenia") ?: ""
        weaponAtaki = intent.getStringExtra("ataki") ?: ""
        weaponKoszt = intent.getStringExtra("koszt") ?: ""
        weaponPociski = intent.getStringExtra("pociski") ?: ""
        weaponUmiejetnosc = intent.getStringExtra("umiejetnosc") ?: ""
        weaponZasieg = intent.getStringExtra("zasieg") ?: ""
        weaponZawodnosc = intent.getStringExtra("zawodnosc") ?: ""
        weaponId = intent.getStringExtra("id") ?: ""


        val addToCardButton: Button = findViewById(R.id.add_to_card_button)
        val deleteWeaponButton: Button = findViewById(R.id.delete_weapon_button)
        val useWeaponButton: Button = findViewById(R.id.use_weapon_button)

        if(added){
            addToCardButton.visibility = View.GONE
            deleteWeaponButton.visibility = View.VISIBLE

        }
        val card = sharedViewModel.skills.value
        val bronPalna = card?.bronPalna



        when(weaponUmiejetnosc){
            "Broń Palna(Krótka)" -> {skillTextView.text = weaponUmiejetnosc
            skillValueTextView.text = bronPalna.toString()
                useWeaponButton.visibility = View.VISIBLE
                skillLayout.visibility = View.VISIBLE
            }
        else -> skillTextView.text = null
        }

        useWeaponButton.setOnClickListener{
            if(bronPalna!=0){
                val result: String?
                val randomNumber = (1..100).random()
                result = if(randomNumber<= bronPalna!!) {
                    "SUKCES"
                } else{
                    "PORAŻKA"
                }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Losowanie umiejętności")
                builder.setMessage("Wylosowano: $randomNumber \n" +
                        "Umiejętność: $bronPalna \n" +
                        "$result")

                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }
            }


        nameTextView.text = weaponNazwa
        damageTextView.text = weaponObrazenia
        atakiTextView.text = weaponAtaki
        kosztTextView.text = weaponKoszt
        pociskiTextView.text = weaponPociski
        umiejetnoscTextView.text = weaponUmiejetnosc
        zasiegTextView.text = weaponZasieg
        zawodnoscTextView.text = weaponZawodnosc

        addToCardButton.setOnClickListener {
            saveWeapon()
        }

        deleteWeaponButton.setOnClickListener(){
            deleteWeaponFromFirebase()
        }




    }

    fun saveWeapon(){
        val weapon = Weapon().apply {
            nazwa = nameTextView.text.toString()
            obrazenia = damageTextView.text.toString()
            ataki = atakiTextView.text.toString()
            koszt = kosztTextView.text.toString()
            pociski = pociskiTextView.text.toString()
            umiejetnosc = umiejetnoscTextView.text.toString()
            zasieg = zasiegTextView.text.toString()
            zawodnosc = zawodnoscTextView.text.toString()
            id = docId
        }
        saveWeaponToFireBase(weapon)
    }


    private fun saveWeaponToFireBase(weapon: Weapon){
        val documentReference: DocumentReference = if (weaponId.isNotEmpty()) {
            // Update the note
            Utility.getCollectionReferenceForWeapons().document(weaponId)
        } else {
            // Create new note
            Utility.getCollectionReferenceForWeapons().document()
        }

        documentReference.set(weapon).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Broń dodana", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(baseContext, "Blad przy dodawaniu broni", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteWeaponFromFirebase() {
        val documentReference: DocumentReference = Utility.getCollectionReferenceForWeapons().document(weaponId)
        documentReference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //note is deleted
                Toast.makeText(baseContext, "Usunięto broń", Toast.LENGTH_SHORT).show()
                Log.d("WIADOMSOC USUWANIA","ID OD USUWANIA TO: $weaponId")
                finish()
            } else {
                Toast.makeText(baseContext, "Note failed to delete", Toast.LENGTH_SHORT).show()

            }
        }
    }


}
