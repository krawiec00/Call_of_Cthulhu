import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.callofcthulhu.Card

class SharedViewModel : ViewModel() {

    private val _card = MutableLiveData<Card?>()
    val card: MutableLiveData<Card?>
        get() = _card

    init {
        _card.value = Card()
    }

    fun updateCardField(fieldName: String, fieldValue: Any) {
        val currentCard = _card.value ?: return // Jeśli card jest null, przerwij

        when (fieldName) {
            "imie" -> {
                if (fieldValue is String) currentCard.imie = fieldValue
            }
            "nazwisko" -> {
                if (fieldValue is String) currentCard.nazwisko = fieldValue
            }
            "profesja" -> {
                if (fieldValue is String) currentCard.profesja = fieldValue
            }
            "wiek" -> {
                if (fieldValue is String) currentCard.wiek = fieldValue
            }
            "plec" -> {
                if (fieldValue is String) currentCard.plec = fieldValue
            }
            "mZamieszkania" -> {
                if (fieldValue is String) currentCard.mZamieszkania = fieldValue
            }
            "mUrodzenia" -> {
                if (fieldValue is String) currentCard.mUrodzenia = fieldValue
            }
            "sila" -> {
                if (fieldValue is Int) currentCard.sila = fieldValue
            }
            "kondycja" -> {
                if (fieldValue is Int) currentCard.kondycja = fieldValue
            }
            "bCiala" -> {
                if (fieldValue is Int) currentCard.bCiala = fieldValue
            }
            "zrecznosc" -> {
                if (fieldValue is Int) currentCard.zrecznosc = fieldValue
            }
            "wyglad" -> {
                if (fieldValue is Int) currentCard.wyglad = fieldValue
            }
            "inteligencja" -> {
                if (fieldValue is Int) currentCard.inteligencja = fieldValue
            }
            "moc" -> {
                if (fieldValue is Int) currentCard.moc = fieldValue
            }
            "wyksztalcenie" -> {
                if (fieldValue is Int) currentCard.wyksztalcenie = fieldValue
            }
            "zycie" -> {
                if (fieldValue is Int) currentCard.zycie = fieldValue
            }
            "poczytalnosc" -> {
                if (fieldValue is Int) currentCard.poczytalnosc = fieldValue
            }
            "szczescie" -> {
                if (fieldValue is Int) currentCard.szczescie = fieldValue
            }
            "magia" -> {
                if (fieldValue is Int) currentCard.magia = fieldValue
            }
            "maxzycie" -> {
                if (fieldValue is Int) currentCard.maxzycie = fieldValue
            }
            "maxmagia" -> {
                if (fieldValue is Int) currentCard.maxmagia = fieldValue
            }
            "ruch" -> {
                if (fieldValue is Int) currentCard.ruch = fieldValue
            }
            "rana" -> {
                if (fieldValue is Boolean) currentCard.rana = fieldValue
            }
            "opis" -> {
                if (fieldValue is String) currentCard.opis = fieldValue
            }
            "ideologia" -> {
                if (fieldValue is String) currentCard.ideologia = fieldValue
            }
            "osoby" -> {
                if (fieldValue is String) currentCard.osoby = fieldValue
            }
            "miejsca" -> {
                if (fieldValue is String) currentCard.miejsca = fieldValue
            }
            "rzeczy" -> {
                if (fieldValue is String) currentCard.rzeczy = fieldValue
            }
            "przymioty" -> {
                if (fieldValue is String) currentCard.przymioty = fieldValue
            }
            "urazy" -> {
                if (fieldValue is String) currentCard.urazy = fieldValue
            }
            "fobie" -> {
                if (fieldValue is String) currentCard.fobie = fieldValue
            }
            "ksiegi" -> {
                if (fieldValue is String) currentCard.ksiegi = fieldValue
            }
            "istoty" -> {
                if (fieldValue is String) currentCard.istoty = fieldValue
            }


            // Dodaj więcej pól w miarę potrzeb
        }

        _card.value = currentCard
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

    fun areFieldsNotEmpty(vararg fieldNames: String): Boolean {
        return fieldNames.all { areFieldsNotEmpty(it) }
    }
}
