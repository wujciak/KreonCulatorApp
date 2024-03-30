package activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kreonculatorapp.R

class UserSelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_select)

        // Tworzenie i konfiguracja Spinnera
        val dropdown: Spinner = findViewById(R.id.selectUserSpinner)
        val items = arrayOf("Pierwszy ziutek")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter

        // Ustawianie paddingu dla systemowych pasków
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Pobierz dane nowego użytkownika z obiektu Intent
        val newUserName = intent.getStringExtra("userName")
        val newUnitsQuantity = intent.getStringExtra("unitsQuantity")

        // Jeśli istnieją dane nowego użytkownika, dodaj go do listy Spinnera
        if (newUserName != null && newUnitsQuantity != null) {
            val newUser = "$newUserName - $newUnitsQuantity g"
            (dropdown.adapter as ArrayAdapter<String>).add(newUser)
        }

        // Nasłuchuje przycisk i przechodzi do CreateUser gdy kliknięty przycisk
        val buttonClick = findViewById<Button>(R.id.newUserButton)
        buttonClick.setOnClickListener {
            val intent = Intent(this, CreateUser::class.java)
            startActivity(intent)
        }

        // Nasłuchuje przycisk i przechodzi do MainActivity gdy kliknięty przycisk
        val buttonNextClick = findViewById<Button>(R.id.goToMealButton)
        buttonNextClick.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}
