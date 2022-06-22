package com.dev_sammi.packagename.guessit.ui.fragments.score

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentScoreBinding
import com.dev_sammi.packagename.guessit.ui.activities.MainActivity
import com.dev_sammi.packagename.guessit.ui.fragments.game.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScoreFragment : Fragment(R.layout.fragment_score){
    private val mScoreViewModel: ScoreViewModel by viewModels()
    private val args : ScoreFragmentArgs by navArgs()
    private lateinit var binding : FragmentScoreBinding
    private lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScoreBinding.bind(view)
        mainActivity = (activity as MainActivity)




        binding.apply {
            lifecycleOwner = this@ScoreFragment
            mViewModel = mScoreViewModel
            mScoreFragment = this@ScoreFragment
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    fun playAgain(){
        findNavController().navigate(
            ScoreFragmentDirections.actionScoreFragmentToPlayFragment()
        )
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(mainActivity.window, false)
        WindowInsetsControllerCompat(mainActivity.window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(mainActivity.window, true)
        WindowInsetsControllerCompat(mainActivity.window, binding.root).let { controller ->
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}