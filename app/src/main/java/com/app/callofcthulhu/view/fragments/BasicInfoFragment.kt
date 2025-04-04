package com.app.callofcthulhu.view.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.app.callofcthulhu.utils.Utility
import com.app.callofcthulhu.view.points.ProfessionPointsActivity
import com.app.callofcthulhu.view.profession.ProfessionList
import com.bumptech.glide.Glide
import java.io.IOException


class BasicInfoFragment : Fragment() {


    private lateinit var imieEditText: EditText
    private lateinit var nazwiskoEditText: EditText
    private lateinit var profesjaEditText: EditText

    //    private lateinit var professionSpinner: Spinner
    private lateinit var wiekEditText: EditText
    private lateinit var plecEditText: EditText
    private lateinit var mieszkanieEditText: EditText
    private lateinit var urodzenieEditText: EditText
    private lateinit var zdjecieButton: Button
    val id = CardDetailsActivity.docId
    private var image: Uri? = null

    var sharedViewModel = SharedViewModelInstance.instance
    private lateinit var imageView: ImageView
    private val MAX_IMAGE_SIZE_BYTES: Long = 20 * 1024 * 1024

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                // Sprawdź wielkość przesyłanego pliku
                try {
                    val selectedImageUri = intent.data
                    if (selectedImageUri != null && selectedImageUri != image) {
                        val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri)
                        if (inputStream != null && inputStream.available() <= MAX_IMAGE_SIZE_BYTES) {
                            image = selectedImageUri
                            Glide.with(this).load(image).into(imageView)
                            sharedViewModel.updateImageUri(image)
                        } else {
                            Toast.makeText(context, "Za duży plik", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(context, "Nie wybrano zdjęcia", Toast.LENGTH_SHORT).show()
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
        val profBtn = view.findViewById<Button>(R.id.prof_btn)

        profBtn.setOnClickListener {
            val intent = Intent(requireContext(), ProfessionList::class.java)
            startActivity(intent)
        }



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


        if (id.isNotEmpty()) {
            profBtn.visibility = View.GONE
            profesjaEditText.visibility = View.VISIBLE
            readData()
        } else {
            sharedViewModel.professionId.observe(viewLifecycleOwner) { professionId ->
                profesjaEditText.setText(professionId)
            }
            profesjaEditText.isFocusable = false
        }




        return view
    }

    private fun attachTextWatcher(editText: EditText, fieldName: String) {
        editText.addTextChangedListener { editable ->
            sharedViewModel.updateCardField(fieldName, editable.toString())
        }
    }

    private fun readData() {
        val docReference = Utility.getCollectionReferenceForCards().document(id)
        docReference.get().addOnSuccessListener { document ->
            document?.let {
                if (it.exists()) {
                    val fields = mapOf(
                        "imie" to imieEditText,
                        "nazwisko" to nazwiskoEditText,
                        "profesja" to profesjaEditText,
                        "wiek" to wiekEditText,
                        "plec" to plecEditText,
                        "mzamieszkania" to mieszkanieEditText,
                        "murodzenia" to urodzenieEditText
                    )

                    fields.forEach { (key, editText) ->
                        editText.setText(document.getString(key))
                    }

                    // Obsługa zdjęcia
                    val imageUrl = document.getString("imageUrl")
                    Glide.with(this)
                        .load(imageUrl)
                        .error(R.drawable.baseline_add_photo_alternate_24)
                        .into(imageView)

                    imageUrl?.let {
                        sharedViewModel.updateCardField("imageUrl", it)
                    }
                }
            }
        }.addOnFailureListener { e ->
            Log.e("TAG", "ERORR: $e")
        }
    }


}