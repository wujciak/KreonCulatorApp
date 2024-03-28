package activities

import android.os.Bundle
import android.widget.*
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

        mealEditText = findViewById(R.id.mealEditText)
        grammatureEditText = findViewById(R.id.grammatureEditText)
        addButton = findViewById(R.id.addButton)
        calcButton = findViewById(R.id.calcButton)
        ingredientList = findViewById(R.id.ingredientList)

        ingredients = ArrayList()

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
            // Implement your calculation logic here based on ingredients list
            // For example, you can calculate the total fat content and then
            // calculate the required dose of Kreon
        }
    }

    private fun refreshIngredientList() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients)
        ingredientList.adapter = adapter
    }
}
