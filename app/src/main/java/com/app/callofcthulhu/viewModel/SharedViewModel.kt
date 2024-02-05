package com.app.callofcthulhu.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.callofcthulhu.model.data.Card
import com.app.callofcthulhu.model.data.Skills


class SharedViewModel : ViewModel() {

    private val _card = MutableLiveData<Card?>()
    val card: MutableLiveData<Card?>
        get() = _card

    init {
        _card.value = Card()

    }

    private val _skills = MutableLiveData<Skills?>()
    val skills: MutableLiveData<Skills?>
        get() = _skills

    init {
        _skills.value = Skills()
    }


    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: MutableLiveData<Uri?>
        get() = _imageUri

    fun updateImageUri(uri: Uri?) {
        _imageUri.value = uri
    }


    private val _professionId = MutableLiveData<String>()
    val professionId: LiveData<String> = _professionId
    fun updateProfessionId(newProfessionId: String) {
        _professionId.value = newProfessionId
    }


    fun updateSkillField(fieldName: String, fieldValue: Any) {
        val currentSkills = _skills.value ?: return

        val newSkills = when (fieldName) {
            "bronPalna" -> currentSkills.copy(bronPalna = fieldValue as? Int)
            "korzystanieZBibliotek" -> currentSkills.copy(korzystanieZBibliotek = fieldValue as? Int)
            "nasluchiwanie" -> currentSkills.copy(nasluchiwanie = fieldValue as? Int)
            "nawigacja" -> currentSkills.copy(nawigacja = fieldValue as? Int)
            "perswazja" -> currentSkills.copy(perswazja = fieldValue as? Int)
            "pierwszaPomoc" -> currentSkills.copy(pierwszaPomoc = fieldValue as? Int)
            "psychologia" -> currentSkills.copy(psychologia = fieldValue as? Int)
            "spostrzegawczosc" -> currentSkills.copy(spostrzegawczosc = fieldValue as? Int)
            "sztukaPrzetrwania" -> currentSkills.copy(sztukaPrzetrwania = fieldValue as? Int)
            "wiedzaONaturze" -> currentSkills.copy(wiedzaONaturze = fieldValue as? Int)
            "plywanie" -> currentSkills.copy(plywanie = fieldValue as? Int)
            "ukrywanie" -> currentSkills.copy(ukrywanie = fieldValue as? Int)
            "walkaWrecz" -> currentSkills.copy(walkaWrecz = fieldValue as? Int)
            "mechanika" -> currentSkills.copy(mechanika = fieldValue as? Int)
            "jezykObcy" -> currentSkills.copy(jezykObcy = fieldValue as? Int)
            "unik" -> currentSkills.copy(unik = fieldValue as? Int)
            "charakteryzacja" -> currentSkills.copy(charakteryzacja = fieldValue as? Int)
            "prawo" -> currentSkills.copy(prawo = fieldValue as? Int)
            "gadanina" -> currentSkills.copy(gadanina = fieldValue as? Int)
            "urokOsobisty" -> currentSkills.copy(urokOsobisty = fieldValue as? Int)
            "zastraszanie" -> currentSkills.copy(zastraszanie = fieldValue as? Int)
            "historia" -> currentSkills.copy(historia = fieldValue as? Int)
            "ksiegowosc" -> currentSkills.copy(ksiegowosc = fieldValue as? Int)
            else -> currentSkills
        }
        _skills.value = newSkills
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
