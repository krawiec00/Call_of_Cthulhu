package com.app.callofcthulhu

import SharedViewModel
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
import com.google.firebase.firestore.DocumentReference


class CardDetailsActivity : AppCompatActivity() {


    private lateinit var sharedViewModel: SharedViewModel

    lateinit var tabLayout: TabLayout
    lateinit var viewPager2: ViewPager2
    private lateinit var myViewPagerAdapter: MyViewPagerAdapter

    private var saveCard: Card? = null

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

        //tabela i fragmenty
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        myViewPagerAdapter = MyViewPagerAdapter(this);
        viewPager2.adapter = myViewPagerAdapter;

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

        pageTitleTextView = findViewById(R.id.page_title)
        saveCardBtn = findViewById(R.id.save_note_btn)
        deleteCardBtn = findViewById(R.id.delete_card_btn)

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
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        sharedViewModel.card.observe(this) { card ->
            saveCard = card
        }


        saveCardBtn.setOnClickListener {
            val requiredFields = listOf("imie", "nazwisko")
            if (sharedViewModel.areFieldsNotEmpty(requiredFields)) {
                saveCard()
            } else {
                Toast.makeText(this, "Imię i nazwisko nie może być puste", Toast.LENGTH_SHORT).show()
            }
        }

        deleteCardBtn.setOnClickListener { showDeleteConfirmationDialog() }




    }


    fun saveCard() {
//        val noteTile = imieEditText.text.toString()
//        val noteContent = nazwiskoEditText.text.toString()
//        if (noteTile == null || noteTile.isEmpty()) {
//            imieEditText.setError("Name")
//            return;
//        }
//        val card = Card()
//        card.imie = imieEdit
//        card.nazwisko = nazwiskoEdit

        saveCard?.let { saveCardToFireBase(it) }
    }

    fun saveCardToFireBase(card: Card) {
        val documentReference: DocumentReference = if (isEdited) {
            // Update the note
            Utility.getCollectionReferenceForCards().document(docId)
        } else {
            // Create new note
            Utility.getCollectionReferenceForCards().document()
        }

        documentReference.set(card).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Note added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(baseContext, "Failed while adding note", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteCardFromFirebase() {
        val documentReference: DocumentReference
        documentReference = Utility.getCollectionReferenceForCards().document(docId)
        documentReference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //note is deleted
                Toast.makeText(baseContext, "Card deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(baseContext, "Card failed to delete", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz usunąć tę kartę?")

        builder.setPositiveButton("Tak") { dialog, _ ->
            deleteCardFromFirebase() // Wywołaj funkcję usuwania po potwierdzeniu
            dialog.dismiss()
        }

        builder.setNegativeButton("Nie") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }


}