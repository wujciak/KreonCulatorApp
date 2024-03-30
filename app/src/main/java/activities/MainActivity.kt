package activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var mealEditText: EditText
    private lateinit var grammatureEditText: EditText
    private lateinit var addButton: Button
    private lateinit var calcButton: Button
    private lateinit var ingredientList: ListView
    private lateinit var ingredients: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        supportActionBar?.title = "KreonCulator"
        mealEditText = findViewById(R.id.mealEditText)
        grammatureEditText = findViewById(R.id.grammatureEditText)
        addButton = findViewById(R.id.addButton)
        calcButton = findViewById(R.id.calcButton)
        ingredientList = findViewById(R.id.ingredientList)
        ingredients = ArrayList()
    }

    private fun setupListeners() {
        addButton.setOnClickListener {
            val meal = mealEditText.text.toString()
            val grammature = grammatureEditText.text.toString()

            if (meal.isNotEmpty() && grammature.isNotEmpty()) {
                val ingredient = "$meal - $grammature g"
                ingredients.add(ingredient)
                refreshIngredientList()
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        calcButton.setOnClickListener {
            // Tu będzie obliczana dawka leku
            // zrobimy to w dalszej części projektu
        }
    }

    private fun refreshIngredientList() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients)
        ingredientList.adapter = adapter
    }

    fun onNewMealButtonClick() {
        val intent = Intent(this, CreateNewMeal::class.java)
        startActivity(intent)
    }
}
