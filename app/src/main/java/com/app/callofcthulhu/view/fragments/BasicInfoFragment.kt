package com.app.callofcthulhu.view.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.app.callofcthulhu.view.card.CardDetailsActivity
import com.app.callofcthulhu.utils.MyApp
import com.app.callofcthulhu.R
import com.app.callofcthulhu.utils.Utility
import com.bumptech.glide.Glide


class BasicInfoFragment : Fragment() {


    private lateinit var imieEditText: EditText
    private lateinit var nazwiskoEditText: EditText
    private lateinit var profesjaEditText: EditText
    private lateinit var professionSpinner: Spinner
    private lateinit var wiekEditText: EditText
    private lateinit var plecEditText: EditText
    private lateinit var mieszkanieEditText: EditText
    private lateinit var urodzenieEditText: EditText
    private lateinit var zdjecieButton: Button
    val id = CardDetailsActivity.docId
    private var image: Uri? = null


    val sharedViewModel = MyApp.sharedViewModel
    private lateinit var imageView: ImageView


    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                image = it.data
                image?.let { selectedImage ->
                    Glide.with(this).load(selectedImage).into(imageView)
                    sharedViewModel.updateImageUri(image)
                }
            }
        } else {
            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_basic_info, container, false)



        imieEditText = view.findViewById(R.id.card_imie)
        nazwiskoEditText = view.findViewById(R.id.card_nazwisko)
        profesjaEditText = view.findViewById(R.id.card_profesja)

        wiekEditText = view.findViewById(R.id.card_wiek)
        plecEditText = view.findViewById(R.id.card_plec)
        mieszkanieEditText = view.findViewById(R.id.card_mZamieszkania)
        urodzenieEditText = view.findViewById(R.id.card_mUrodzenia)

        imageView = view.findViewById(R.id.card_zdjecie)

        zdjecieButton = view.findViewById(R.id.btn_dodaj_zdjecie)






        zdjecieButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResultLauncher.launch(intent)

        }


// Wywołanie funkcji attachTextWatcher dla każdego EditText
        attachTextWatcher(imieEditText, "imie")
        attachTextWatcher(nazwiskoEditText, "nazwisko")
        attachTextWatcher(profesjaEditText, "profesja")
        attachTextWatcher(wiekEditText, "wiek")
        attachTextWatcher(plecEditText, "plec")
        attachTextWatcher(mieszkanieEditText, "mZamieszkania")
        attachTextWatcher(urodzenieEditText, "mUrodzenia")



        fun setupSpinner() {
            // Inicjalizacja Spinnera i adaptera
            professionSpinner = view.findViewById(R.id.card_spinner_profesja)
            val professions =
                arrayOf("Detektyw Policyjny", "Duchowny", "Zolnierz") // Twoja lista profesji
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, professions)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            professionSpinner.adapter = adapter

            // Listener dla wyboru elementu w Spinnerze
            professionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedProfession = professions[position]
                    sharedViewModel.updateCardField("profesja", selectedProfession)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Obsługa sytuacji, gdy nie jest wybrany żaden element
                }
            }
        }


        setupSpinner()

//        sharedViewModel.cardData.observe(viewLifecycleOwner) { card ->
//            card?.let {
//                // Aktualizacja interfejsu użytkownika na podstawie danych karty
//                imieEditText.setText(card.imie)
//                nazwiskoEditText.setText(card.nazwisko)
//                profesjaEditText.setText(card.profesja)
//                wiekEditText.setText(card.wiek)
//                plecEditText.setText(card.plec)
//                mieszkanieEditText.setText(card.mZamieszkania)
//                urodzenieEditText.setText(card.mUrodzenia)
//
//                Glide.with(this)
//                    .load(card.imageUrl) // źródło obrazu (adres URL)
//                    .into(imageView) // ImageView, do którego chcesz załadować obraz
//                if (card.imageUrl != null) {
//                    sharedViewModel.updateCardField("imageUrl", card.imageUrl!!)
//                }
//            }
//        }
//
//        if (id.isNotEmpty()) {
//            sharedViewModel.loadCardData(id)
//        }


        if (id.isNotEmpty()) {
            professionSpinner.visibility = View.GONE
            profesjaEditText.visibility = View.VISIBLE
            val docReference = Utility.getCollectionReferenceForCards().document(id)
            docReference.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val imie = document.getString("imie")
                    val nazwisko = document.getString("nazwisko")
                    val profesja = document.getString("profesja")
                    val wiek = document.getString("wiek")
                    val plec = document.getString("plec")
                    val mzamieszkania = document.getString("mzamieszkania")
                    val murodzenia = document.getString("murodzenia")
                    val imageUrl = document.getString("imageUrl")

                    // Przypisanie pobranych danych do pól EditText w fragmencie
                    imieEditText.setText(imie)
                    nazwiskoEditText.setText(nazwisko)
                    profesjaEditText.setText(profesja)
                    wiekEditText.setText(wiek)
                    plecEditText.setText(plec)
                    mieszkanieEditText.setText(mzamieszkania)
                    urodzenieEditText.setText(murodzenia)

                        // Użycie biblioteki Glide do wyświetlenia obrazu w ImageView
                        Glide.with(this)
                            .load(imageUrl) // źródło obrazu (adres URL)
                            .into(imageView) // ImageView, do którego chcesz załadować obraz
                    if (imageUrl != null) {
                        sharedViewModel.updateCardField("imageUrl", imageUrl)
                    }
                }
            }
        }

        return view
    }

    private fun attachTextWatcher(editText: EditText, fieldName: String) {
        editText.addTextChangedListener { editable ->
            sharedViewModel.updateCardField(fieldName, editable.toString())
        }
    }


}