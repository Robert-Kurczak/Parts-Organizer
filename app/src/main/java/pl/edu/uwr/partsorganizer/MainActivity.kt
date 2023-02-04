package pl.edu.uwr.partsorganizer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pl.edu.uwr.partsorganizer.databinding.ActivityMainBinding
import pl.edu.uwr.partsorganizer.model.DataProvider

class MainActivity : AppCompatActivity(){

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
//    private val database = DataProvider.getDatabase(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

    }
}
