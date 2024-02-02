package com.app.callofcthulhu.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.app.callofcthulhu.R
import com.app.callofcthulhu.utils.Utility
import com.app.callofcthulhu.utils.Utility.Companion.getCollectionReferenceForCards
import com.app.callofcthulhu.utils.Utility.Companion.getCollectionReferenceForNotes
import com.app.callofcthulhu.utils.Utility.Companion.getCollectionReferenceForSpells
import com.app.callofcthulhu.utils.Utility.Companion.getCollectionReferenceForWeapons
import com.app.callofcthulhu.model.data.Card
import com.app.callofcthulhu.view.card.CardAdapter
import com.app.callofcthulhu.view.authorization.LoginActivity
import com.app.callofcthulhu.view.card.CardDetailsActivity
import com.app.callofcthulhu.view.share.ShareNotificationActivity
import com.app.callofcthulhu.view.users.UsersListActivity
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userText: TextView
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        val cardButton = findViewById<FloatingActionButton>(R.id.add_note_btn)
        recyclerView = findViewById(R.id.recycler_view)


        //wyświetlanie karty
        cardButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, CardDetailsActivity::class.java))
        }
        setupRecyclerView()




        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        val headerView = navigationView.getHeaderView(0)
        userText = headerView.findViewById(R.id.userText)

        if (user == null) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            userText.text = user.email
        }

        val menu: Menu = navigationView.menu
        val menuItem: MenuItem? = menu.findItem(R.id.nav_panel)


        val uid = user?.uid

        val db = FirebaseFirestore.getInstance()

        if (uid != null) {
            val userLogRef = db.collection("user_logs").document(uid)

            userLogRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val role = documentSnapshot.getString("role")
                        // Tutaj możesz wykorzystać pobraną rolę (np. wyświetlić, przekazać dalej, etc.)
                        if (role != "admin")
                            menuItem?.isVisible = false
                    }
                }
                .addOnFailureListener {
                    // Wystąpił błąd podczas pobierania danych
                }
        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Witaj badaczu"

        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_notification -> {
                val intent = Intent(applicationContext, ShareNotificationActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_reset -> passwordReset()


            R.id.nav_delete -> deleteAllUserData()


            R.id.nav_logout -> logout()

            R.id.nav_panel -> {
                val intent = Intent(applicationContext, UsersListActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun deleteCollection(collection: CollectionReference, batchSize: Int): Task<Void> {
        val query = collection.limit(batchSize.toLong())

        return query.get().continueWithTask { snapshot ->
            val batch = collection.firestore.batch()
            snapshot.result?.forEach { document ->
                batch.delete(document.reference)
            }

            return@continueWithTask batch.commit().continueWithTask {
                if (snapshot.result!!.size() < batchSize) {
                    return@continueWithTask null
                } else {
                    return@continueWithTask deleteCollection(collection, batchSize)
                }
            }
        }
    }

    private fun passwordReset() {
        if (user != null) {
            val email = user.email
            if (email != null) {


                Utility.writeLogToFirebase("Reset hasła")

                val confirmationDialog = AlertDialog.Builder(this)
                confirmationDialog.setTitle("Potwierdzenie")
                confirmationDialog.setMessage("Czy na pewno chcesz zresetować hasło?")
                confirmationDialog.setPositiveButton("Tak") { _, _ ->
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                logout()
                                Toast.makeText(
                                    this,
                                    "Wysłano maila z resetem hasła",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Wystąpił problem z wysłaniem maila",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
                confirmationDialog.setNegativeButton("Anuluj") { dialog, _ ->
                    dialog.dismiss()
                }
                confirmationDialog.show()
            }
        }
    }


    private fun deleteAllUserData() {
        val confirmationDialog = AlertDialog.Builder(this)
        confirmationDialog.setTitle("Potwierdzenie usunięcia konta")
        confirmationDialog.setMessage("Czy na pewno chcesz usunąć swoje konto? Tej operacji nie można cofnąć.")
        confirmationDialog.setPositiveButton("Tak") { _, _ ->
            val collectionRef = getCollectionReferenceForCards()
            collectionRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val skillsRef = collectionRef.document(document.id).collection("Skills")
                    deleteCollection(skillsRef, 50).addOnCompleteListener {
                        // Po usunięciu podkolekcji `Skills` usuń dokument `Card`
                        collectionRef.document(document.id).delete()
                    }
                }

                // Usuwanie innych kolekcji i konta użytkownika po usunięciu wszystkich `Cards`
                deleteOtherCollectionsAndUserAccount()
            }
        }
        confirmationDialog.setNegativeButton("Anuluj") { dialog, _ ->
            dialog.dismiss()
        }
        confirmationDialog.show()
    }

    private fun deleteOtherCollectionsAndUserAccount() {
        val collectionRefNotes = getCollectionReferenceForNotes()
        val collectionRefWeapons = getCollectionReferenceForWeapons()
        val collectionRefSpells = getCollectionReferenceForSpells()

        val deleteNotesTask = deleteCollection(collectionRefNotes, 50)
        val deleteWeaponsTask = deleteCollection(collectionRefWeapons, 50)
        val deleteSpellsTask = deleteCollection(collectionRefSpells, 50)

        Tasks.whenAllSuccess<Void>(
            deleteNotesTask,
            deleteWeaponsTask,
            deleteSpellsTask
        ).addOnSuccessListener {
            // Po zakończeniu usuwania wszystkich kolekcji, usuń użytkownika
            deleteUserAccount()
        }.addOnFailureListener {

        }
        deleteUserAccount()
    }

    private fun deleteUserAccount() {
        val userProfile = FirebaseAuth.getInstance().currentUser
        Utility.writeLogToFirebase("Usunięto konto")

        userProfile?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                goToLoginPage()
                Toast.makeText(this, "Usunięto konto", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Błąd podczas usuwania konta", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun goToLoginPage() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun logout() {
        if (auth.currentUser != null) {
            val confirmationDialog = AlertDialog.Builder(this)
            confirmationDialog.setTitle("Potwierdzenie wylogowania")
            confirmationDialog.setMessage("Czy na pewno chcesz się wylogować?")
            confirmationDialog.setPositiveButton("Tak") { _, _ ->
                Utility.writeLogToFirebase("Wylogowanie")
                Firebase.auth.signOut()
                goToLoginPage()
                Toast.makeText(this, "Wylogowano", Toast.LENGTH_SHORT).show()
            }
            confirmationDialog.setNegativeButton("Anuluj") { dialog, _ ->
                dialog.dismiss()
            }
            confirmationDialog.show()
        } else {
            Toast.makeText(baseContext, "Niezalogowany", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupRecyclerView() {
        val query: Query =
            getCollectionReferenceForCards().orderBy("imie", Query.Direction.ASCENDING)
        val options: FirestoreRecyclerOptions<Card> = FirestoreRecyclerOptions.Builder<Card>()
            .setQuery(query, Card::class.java)
            .build()
        recyclerView.layoutManager = LinearLayoutManager(this)
        cardAdapter = CardAdapter(options, this)
        recyclerView.adapter = cardAdapter
    }

    override fun onStart() {
        super.onStart()
        cardAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        cardAdapter.stopListening()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        cardAdapter.notifyDataSetChanged()
        cardAdapter.startListening()
    }

}

