package com.stream.locomotion

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.stream.locomotion.ui.guide.GuideFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Loads [GuideFragment].
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_browse_fragment, GuideFragment())
                .commitNow()
        }
    }
}
