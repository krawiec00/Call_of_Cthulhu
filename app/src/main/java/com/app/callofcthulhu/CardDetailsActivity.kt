package com.app.callofcthulhu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference


class CardDetailsActivity : AppCompatActivity() {


    //private lateinit var sharedViewModel: SharedViewModel

    lateinit var tabLayout: TabLayout
    lateinit var viewPager2: ViewPager2
    private lateinit var myViewPagerAdapter: MyViewPagerAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var saveCard: Card? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var pageTitleTextView: TextView
    private var isEdited: Boolean = false
    private lateinit var deleteCardBtn: ImageButton
    private lateinit var saveCardBtn: ImageButton
    private var imie: String? = null
    private var nazwisko: String? = null

    companion object {
        var docId: String = ""
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        pageTitleTextView = findViewById(R.id.page_title)
        saveCardBtn = findViewById(R.id.save_note_btn)
        deleteCardBtn = findViewById(R.id.delete_card_btn)
        auth = Firebase.auth
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        //pobrane dane do wyświetlenia do edycji
        imie = intent.getStringExtra("imie")
        nazwisko = intent.getStringExtra("nazwisko")
        docId = intent.getStringExtra("docId") ?: ""

        if (docId.isNotEmpty()) {
            isEdited = true
        }

//        imieEditText.setText(imie)
//        nazwiskoEditText.setText(nazwisko)
        if (isEdited) {
            pageTitleTextView.text = "$imie $nazwisko"
            deleteCardBtn.visibility = View.VISIBLE
        }

        //viewModel przekazuje dane z modelu, cała instancja card
        val sharedViewModel = MyApp.sharedViewModel

        sharedViewModel.card.observe(this) { card ->
            saveCard = card
        }


        saveCardBtn.setOnClickListener {
            val requiredFields = listOf("imie", "nazwisko")
            if (sharedViewModel.areFieldsNotEmpty(requiredFields)) {
                saveCard()
            } else {
                Toast.makeText(this, "Imię i nazwisko nie może być puste", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        deleteCardBtn.setOnClickListener { showDeleteConfirmationDialog() }


        //tabela i fragmenty
        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager)
        myViewPagerAdapter = MyViewPagerAdapter(this)
        viewPager2.adapter = myViewPagerAdapter

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager2.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)!!.select()
            }
        })


    }


    private fun saveCard() {

        saveCard?.let { saveCardToFireBase(it) }
    }

    private fun saveCardToFireBase(card: Card) {

        val documentReference: DocumentReference = if (isEdited) {
            // Update
            Utility.getCollectionReferenceForCards().document(docId)

        } else {
            // Create new
            Utility.getCollectionReferenceForCards().document()
        }

        documentReference.set(card)


                if (docId.isNotEmpty()) {
                    Utility.writeLogToFirebase("Aktualizacja karty")

                    Toast.makeText(baseContext, "Zaktualizowano kartę", Toast.LENGTH_SHORT).show()
                } else {
                    Utility.writeLogToFirebase("Stworzenie karty")

                    Toast.makeText(baseContext, "Dodano kartę", Toast.LENGTH_SHORT).show()
                    finish()
                }

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Stworzenie karty")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle)


        }


    private fun deleteCardFromFirebase() {
        val documentReference: DocumentReference =
            Utility.getCollectionReferenceForCards().document(docId)
        documentReference.delete()

                Utility.writeLogToFirebase("Usunięcie karty")

                Toast.makeText(baseContext, "Usunięto karte", Toast.LENGTH_SHORT).show()
                finish()


    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz usunąć tę kartę?")

        builder.setPositiveButton("Tak") { dialog, _ ->
            deleteCardFromFirebase()
            deleteAllNotesForCardFromFirebase(docId)
            deleteAllSpellsForCardFromFirebase(docId)
            deleteAllWeaponsForCardFromFirebase(docId)
            dialog.dismiss()
        }

        builder.setNegativeButton("Nie") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }

    private fun deleteAllNotesForCardFromFirebase(cardId: String) {
        val notesCollection = Utility.getCollectionReferenceForNotes()
        notesCollection
            .whereEqualTo("id", cardId) // Zmodyfikuj to pole na odpowiednie
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        document.reference.delete()
                    }
                }
            }
    }

    private fun deleteAllSpellsForCardFromFirebase(cardId: String) {
        val spellsCollection = Utility.getCollectionReferenceForSpells()
        spellsCollection
            .whereEqualTo("id", cardId) // Zmodyfikuj to pole na odpowiednie
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        document.reference.delete()
                    }

                }
            }
    }

    private fun deleteAllWeaponsForCardFromFirebase(cardId: String) {
        val weaponsCollection = Utility.getCollectionReferenceForWeapons()
        weaponsCollection
            .whereEqualTo("id", cardId) // Zmodyfikuj to pole na odpowiednie
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        document.reference.delete()
                    }

                }
            }
    }


}