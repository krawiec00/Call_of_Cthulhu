package com.app.callofcthulhu.view.card

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.app.callofcthulhu.utils.MyApp
import com.app.callofcthulhu.R
import com.app.callofcthulhu.utils.Utility
import com.app.callofcthulhu.model.data.Card
import com.app.callofcthulhu.model.repository.CardRepository
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.app.callofcthulhu.view.share.ShareCardActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


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
    private lateinit var shareCardBtn: ImageButton
    private lateinit var saveCardBtn: ImageButton
    private var imie: String? = null
    private var nazwisko: String? = null
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference
    //    private lateinit var progressIndicator: LinearProgressIndicator
    private val cardRepository = CardRepository()
//    val sharedViewModel = MyApp.sharedViewModel
    val sharedViewModel = SharedViewModelInstance.instance

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
        shareCardBtn = findViewById(R.id.share_card_btn)
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
            shareCardBtn.visibility = View.VISIBLE
        }

        //viewModel przekazuje dane z modelu, cała instancja card


        sharedViewModel.card.observe(this) { card ->
            saveCard = card

        }

        sharedViewModel.imageUri.observe(this) {
            imageUri = it
        }

        storageReference = FirebaseStorage.getInstance().reference


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

        shareCardBtn.setOnClickListener {
            val intent = Intent(this, ShareCardActivity::class.java)
            val nazwa = "$imie $nazwisko"
            intent.putExtra("nazwa", nazwa)
//            intent.putExtra("nazwisko", nazwisko)
            startActivity(intent)
        }

        //tabela i fragmenty
        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager)
        myViewPagerAdapter = MyViewPagerAdapter(this)
        viewPager2.adapter = myViewPagerAdapter
        viewPager2.offscreenPageLimit = 5
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

        saveCard?.let { cardRepository.saveCardToFireBase(it, imageUri) }

        if (isEdited) {
            Toast.makeText(baseContext, "Zaktualizowano kartę", Toast.LENGTH_SHORT).show()
            Utility.writeLogToFirebase("Aktualizacja karty")
        } else {
            Toast.makeText(baseContext, "Dodano kartę", Toast.LENGTH_SHORT).show()
            Utility.writeLogToFirebase("Utworzenie karty")
            finish()
        }

    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz usunąć tę kartę?")

        builder.setPositiveButton("Tak") { dialog, _ ->
            cardRepository.deleteImage()
            cardRepository.deleteCardFromFirebase()
            cardRepository.deleteFromFirebase(docId, "my_weapons")
            cardRepository.deleteFromFirebase(docId, "my_spells")
            cardRepository.deleteFromFirebase(docId, "my_notes")
            Toast.makeText(baseContext, "Usunięto karte", Toast.LENGTH_SHORT).show()
            finish()
            dialog.dismiss()
        }

        builder.setNegativeButton("Nie") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz opuścić tę stronę? Upewnij się że zapisałeś zmiany.")

        builder.setPositiveButton("Wyjdź") { _, _ ->
            super.onBackPressed()
        }
        if (isEdited) {
            builder.setNeutralButton("Zapisz i wyjdź") { _, _ ->
                saveCard()
                super.onBackPressed()
            }
        }
        builder.setNegativeButton("Anuluj") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        SharedViewModelInstance.clearInstance()
    }


}