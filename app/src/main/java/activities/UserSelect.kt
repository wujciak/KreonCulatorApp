package activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kreonculatorapp.R

class UserSelect : AppCompatActivity() {
    private lateinit var dropdown: Spinner
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_select)
        supportActionBar?.title = "Select User"
        dropdown = findViewById(R.id.selectUserSpinner)
        val items = arrayOf("Pierwszy ziutek")
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val newUserName = intent.getStringExtra("userName")
        val newUnitsQuantity = intent.getStringExtra("unitsQuantity")

        if (newUserName != null && newUnitsQuantity != null) {
            val newUser = "$newUserName - $newUnitsQuantity g"
            Handler(Looper.getMainLooper()).post {
                adapter.add(newUser)
                adapter.notifyDataSetChanged()
            }
        }

        val buttonClick = findViewById<Button>(R.id.newUserButton)
        buttonClick.setOnClickListener {
            val intent = Intent(this, CreateUser::class.java)
            startActivity(intent)
        }

        val buttonNextClick = findViewById<Button>(R.id.goToMealButton)
        buttonNextClick.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
