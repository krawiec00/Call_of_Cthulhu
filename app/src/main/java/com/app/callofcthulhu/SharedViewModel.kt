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
            // Dodaj więcej pól w miarę potrzeb
        }

        _card.value = currentCard
    }

    fun isFieldNotEmpty(fieldName: String): Boolean {
        val card = _card.value ?: return false // Jeśli card jest null, zwróć false

        return when (fieldName) {
            "imie" -> !card.imie.isNullOrBlank()
            "nazwisko" -> !card.nazwisko.isNullOrBlank()
            // Dodaj więcej pól w miarę potrzeb
            else -> false // Domyślnie zwróć false, jeśli pole nie zostało znalezione
        }
    }

    fun areFieldsNotEmpty(vararg fieldNames: String): Boolean {
        return fieldNames.all { isFieldNotEmpty(it) }
    }
}
