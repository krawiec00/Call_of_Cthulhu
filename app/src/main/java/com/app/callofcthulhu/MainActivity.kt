package com.app.callofcthulhu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForCards
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForNotes
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForSpells
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForWeapons
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference

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
        cardButton.setOnClickListener() {
            startActivity(Intent(this@MainActivity, CardDetailsActivity::class.java))
        }
        setupRecyclerView()



        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        val headerView = navigationView.getHeaderView(0)
        userText = headerView.findViewById(R.id.userText)

        if (user == null) {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        } else {
            userText.text = user.email
        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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
            R.id.nav_reset -> {
                if (user != null) {
                    val email = user.email
                    if (email != null) {
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

                }
            }


            R.id.nav_delete -> deleteUser()


            R.id.nav_logout -> logout()
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

    private fun deleteUser() {
        val collectionRef = getCollectionReferenceForCards()
        val collectionRefNotes = getCollectionReferenceForNotes()
        val collectionRefWeapons = getCollectionReferenceForWeapons()
        val collectionRefSpells = getCollectionReferenceForSpells()


        val deleteNotesTask = deleteCollection(collectionRefNotes, 50)
        val deleteWeaponsTask = deleteCollection(collectionRefWeapons, 50)
        val deleteSpellsTask = deleteCollection(collectionRefSpells, 50)
        val deleteCardsTask = deleteCollection(collectionRef, 50)

        Tasks.whenAllSuccess<Void>(
            deleteNotesTask,
            deleteWeaponsTask,
            deleteSpellsTask,
            deleteCardsTask
        ).addOnSuccessListener {
            // Po zakończeniu usuwania wszystkich kolekcji, usuń użytkownika
            deleteUserAccount()
        }.addOnFailureListener { exception ->
//            // Obsłuż błąd usuwania kolekcji
//            Toast.makeText(this, "Błąd podczas usuwania kolekcji: $exception", Toast.LENGTH_SHORT)
//                .show()
//            Log.e("TEST", "BŁĄD: $exception")
//            Log.e("TEST", "UZYTKOWNIK: $user")

        }
        deleteUserAccount()
    }

    private fun deleteUserAccount() {
        val userProfile = FirebaseAuth.getInstance().currentUser
        userProfile?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                goToLoginPage()
                Toast.makeText(this, "Usunięto konto", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Błąd podczas usuwania", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun goToLoginPage() {
        val intent = Intent(applicationContext, Login::class.java)
        startActivity(intent)
        finish()
    }

    private fun logout() {
        if (auth.currentUser != null) {
            Firebase.auth.signOut()
            goToLoginPage()
        } else {
            Toast.makeText(baseContext, "niezalogowany", Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(this, "Wylogowano", Toast.LENGTH_SHORT).show()
    }


    private fun setupRecyclerView() {
        val query: Query =
            getCollectionReferenceForCards().orderBy("imie", Query.Direction.DESCENDING)
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
    }

}

