package com.app.callofcthulhu.viewModel

import android.net.Uri

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.callofcthulhu.model.data.Card


class SharedViewModel : ViewModel() {

    private val _card = MutableLiveData<Card?>()
    val card: MutableLiveData<Card?>
        get() = _card

    init {
        _card.value = Card()
    }

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: MutableLiveData<Uri?>
        get() = _imageUri

    fun updateImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun updateCardField(fieldName: String, fieldValue: Any) {
        val currentCard = _card.value ?: return // Jeśli card jest null, przerwij

        val newCard = when (fieldName) {
            "imie" -> currentCard.copy(imie = fieldValue as? String)
            "nazwisko" -> currentCard.copy(nazwisko = fieldValue as? String)
            "profesja" -> currentCard.copy(profesja = fieldValue as? String)
            "wiek" -> currentCard.copy(wiek = fieldValue as? String)
            "plec" -> currentCard.copy(plec = fieldValue as? String)
            "mZamieszkania" -> currentCard.copy(mZamieszkania = fieldValue as? String)
            "mUrodzenia" -> currentCard.copy(mUrodzenia = fieldValue as? String)
            "sila" -> currentCard.copy(sila = fieldValue as? Int)
            "kondycja" -> currentCard.copy(kondycja = fieldValue as? Int)
            "bCiala" -> currentCard.copy(bCiala = fieldValue as? Int)
            "zrecznosc" -> currentCard.copy(zrecznosc = fieldValue as? Int)
            "wyglad" -> currentCard.copy(wyglad = fieldValue as? Int)
            "inteligencja" -> currentCard.copy(inteligencja = fieldValue as? Int)
            "moc" -> currentCard.copy(moc = fieldValue as? Int)
            "wyksztalcenie" -> currentCard.copy(wyksztalcenie = fieldValue as? Int)
            "ruch" -> currentCard.copy(ruch = fieldValue as? Int)
            "zycie" -> currentCard.copy(zycie = fieldValue as? Int)
            "maxzycie" -> currentCard.copy(maxzycie = fieldValue as? Int)
            "poczytalnosc" -> currentCard.copy(poczytalnosc = fieldValue as? Int)
            "szczescie" -> currentCard.copy(szczescie = fieldValue as? Int)
            "magia" -> currentCard.copy(magia = fieldValue as? Int)
            "maxmagia" -> currentCard.copy(maxmagia = fieldValue as? Int)
            "rana" -> currentCard.copy(rana = fieldValue as? Boolean)
            "opis" -> currentCard.copy(opis = fieldValue as? String)
            "ideologia" -> currentCard.copy(ideologia = fieldValue as? String)
            "osoby" -> currentCard.copy(osoby = fieldValue as? String)
            "miejsca" -> currentCard.copy(miejsca = fieldValue as? String)
            "rzeczy" -> currentCard.copy(rzeczy = fieldValue as? String)
            "przymioty" -> currentCard.copy(przymioty = fieldValue as? String)
            "urazy" -> currentCard.copy(urazy = fieldValue as? String)
            "fobie" -> currentCard.copy(fobie = fieldValue as? String)
            "ksiegi" -> currentCard.copy(ksiegi = fieldValue as? String)
            "istoty" -> currentCard.copy(istoty = fieldValue as? String)
            "Bron_Palna" -> currentCard.copy(Bron_Palna = fieldValue as? Int)
            "Korzystanie_z_Bibliotek" -> currentCard.copy(Korzystanie_z_Bibliotek = fieldValue as? Int)
            "Nasluchiwanie" -> currentCard.copy(Nasluchiwanie = fieldValue as? Int)
            "Nawigacja" -> currentCard.copy(Nawigacja = fieldValue as? Int)
            "Perswazja" -> currentCard.copy(Perswazja = fieldValue as? Int)
            "Pierwsza_Pomoc" -> currentCard.copy(Pierwsza_Pomoc = fieldValue as? Int)
            "Psychologia" -> currentCard.copy(Psychologia = fieldValue as? Int)
            "Spostrzegawczosc" -> currentCard.copy(Spostrzegawczosc = fieldValue as? Int)
            "Sztuka_Przetrwania" -> currentCard.copy(Sztuka_Przetrwania = fieldValue as? Int)
            "Wiedza_o_Naturze" -> currentCard.copy(Wiedza_o_Naturze = fieldValue as? Int)
            "gotowka" -> currentCard.copy(gotowka = fieldValue as? String)
            "dobytek" -> currentCard.copy(dobytek = fieldValue as? String)
            "przedmioty" -> currentCard.copy(przedmioty = fieldValue as? String)
            "wydatki" -> currentCard.copy(wydatki = fieldValue as? String)
            "imageUrl" -> currentCard.copy(imageUrl = fieldValue as? String)
            else -> currentCard
        }

        _card.value = newCard
    }

    fun areFieldsNotEmpty(fieldNames: List<String>): Boolean {
        val card = _card.value ?: return false // Jeśli card jest null, zwróć false

        return fieldNames.all { fieldName ->
            when (fieldName) {
                "imie" -> !card.imie.isNullOrBlank()
                "nazwisko" -> !card.nazwisko.isNullOrBlank()
                // Dodaj więcej pól w miarę potrzeb
                else -> false // Domyślnie zwróć false, jeśli pole nie zostało znalezione
            }
        }
    }

}
