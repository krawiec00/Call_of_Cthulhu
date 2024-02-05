package com.app.callofcthulhu.view.fragments

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.R
import com.app.callofcthulhu.model.data.SkillItem
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.app.callofcthulhu.view.card.CardDetailsActivity


class SkillsAdapter(private val context: Context, private val skillItems: List<SkillItem>) :
    RecyclerView.Adapter<SkillsAdapter.SkillViewHolder>() {
    val sharedViewModel = SharedViewModelInstance.instance

    class SkillViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val skillNameTextView: TextView = view.findViewById(R.id.skill_name)
        val skillValueEditText: EditText = view.findViewById(R.id.skill_value)
        val skillHalfValue: TextView = view.findViewById(R.id.skill_little_value)
        val skillFifthValue: TextView = view.findViewById(R.id.skill_little_value2)
        val skillUpButton: ImageButton = view.findViewById(R.id.button_upgrade)
        val skillCheckButton: ImageButton = view.findViewById(R.id.button_skill_check)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_skill_item, parent, false)
        return SkillViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {

        val skillItem = skillItems[position]
        val cardId = CardDetailsActivity.docId
        holder.skillNameTextView.text = skillItem.friendlyName
        holder.skillValueEditText.setText(skillItem.skillValue.toString())

        holder.checkBox.isChecked = skillItem.isChecked

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            skillItem.isChecked = isChecked
            // Zapis stanu w lokalnej pamięci
            saveCheckedStateToLocalMemory(cardId, skillItem.technicalName, isChecked)
        }
        holder.checkBox.isChecked = loadCheckedStateFromLocalMemory(cardId, skillItem.technicalName)

        // Dodanie TextWatcher do EditText
        holder.skillValueEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tutaj nic
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Tutaj również nic
            }

            override fun afterTextChanged(editable: Editable?) {
                val value = editable.toString().toIntOrNull() ?: 0
                skillItem.skillValue = value
                // Użyj technicalName do aktualizacji ViewModel
                sharedViewModel.updateSkillField(skillItem.technicalName, value)
                holder.skillHalfValue.text = (value / 2).toString()
                holder.skillFifthValue.text = (value / 5).toString()
            }
        })

        val halfValue = skillItem.skillValue / 2
        val fifthValue = skillItem.skillValue / 5

        holder.skillHalfValue.text = halfValue.toString()
        holder.skillFifthValue.text = fifthValue.toString()

        holder.skillUpButton.setOnClickListener {
            val value = skillItem.skillValue
            var message = ""
            var randomNumber = 0
            if (value <= 50) {
                randomNumber = (1..10).random() + value
                holder.skillValueEditText.setText(randomNumber.toString())
                message = "Rozwój umiejętności o ${randomNumber - value}"
            } else {
                val check = (1..100).random()
                if (check > value) {
                    randomNumber = (1..10).random() + value
                    message =
                        "Umiejętność: $value \nWylosowano: $check \nRozwój o: ${randomNumber - value}"
                    holder.skillValueEditText.setText(randomNumber.toString())

                } else {
                    message = "Umiejętność: $value \nWylosowano: $check \nBrak rozwoju"
                }
            }
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder
                .setTitle("Rozwój umiejętności")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        holder.skillCheckButton.setOnClickListener {
            val value = skillItem.skillValue
            val check = (1..100).random()
            var message = ""
            message = if (check <= value)
                "Umiejętność: $value \nWylosowano: $check \nSukces"
            else
                "Umiejętność: $value \nWylosowano: $check \nPorażka"

            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder
                .setTitle("Rzut na umiejętność")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

        }


    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    private fun saveCheckedStateToLocalMemory(cardId: String, skillName: String, isChecked: Boolean) {
        val sharedPrefs = context.getSharedPreferences("SkillPrefs", Context.MODE_PRIVATE)
        val key = "$cardId-$skillName" // Tworzy unikalny klucz
        sharedPrefs.edit().putBoolean(key, isChecked).apply()
    }

    private fun loadCheckedStateFromLocalMemory(cardId: String, skillName: String): Boolean {
        val sharedPrefs = context.getSharedPreferences("SkillPrefs", Context.MODE_PRIVATE)
        val key = "$cardId-$skillName" // Tworzy unikalny klucz
        return sharedPrefs.getBoolean(key, false)
    }


    override fun getItemCount() = skillItems.size
}
