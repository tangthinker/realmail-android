package work.tangthinker.realmail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import work.tangthinker.realmail.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAboutBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.bottom_silent, R.anim.horizontal_close)
    }



}