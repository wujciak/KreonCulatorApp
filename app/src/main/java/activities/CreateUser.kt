package activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kreonculatorapp.R

class CreateUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_user)
        supportActionBar?.title = "Create User"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonClick = findViewById<Button>(R.id.addUserButton)
        buttonClick.setOnClickListener {
            val userNameEditText = findViewById<EditText>(R.id.newUserEditText)
            val unitsQuantityEditText = findViewById<EditText>(R.id.setQuantityEditText)

            val userName = userNameEditText.text.toString()
            val unitsQuantity = unitsQuantityEditText.text.toString()

            // nie działa
            if (userName.isNotEmpty() && unitsQuantity.isNotEmpty()) {
                val intent = Intent(this, UserSelect::class.java).apply {
                    putExtra("userName", userName)
                    putExtra("unitsQuantity", unitsQuantity)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
