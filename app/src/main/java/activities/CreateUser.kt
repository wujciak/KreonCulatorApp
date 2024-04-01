package activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R

class CreateUser : AppCompatActivity() {

    private lateinit var setQuantityEditText: EditText
    private lateinit var addUserButton: Button
    private lateinit var newUserEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        supportActionBar?.title = "Create User"
        setQuantityEditText = findViewById(R.id.setQuantityEditText)
        newUserEditText = findViewById(R.id.newUserEditText)
        addUserButton = findViewById(R.id.addUserButton)
    }

    private fun setupListeners() {
        addUserButton.setOnClickListener {
            val userName = newUserEditText.text.toString()
            val unitsQuantity = setQuantityEditText.text.toString()
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
