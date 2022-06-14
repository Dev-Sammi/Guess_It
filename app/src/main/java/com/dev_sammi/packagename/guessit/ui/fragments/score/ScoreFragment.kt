package com.dev_sammi.packagename.guessit.ui.fragments.score

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentScoreBinding
import com.dev_sammi.packagename.guessit.ui.fragments.game.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScoreFragment : Fragment(R.layout.fragment_score){
    private val mGameViewModel: GameViewModel by activityViewModels()
    private lateinit var binding : FragmentScoreBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScoreBinding.bind(view)
        binding.apply {
            lifecycleOwner = this@ScoreFragment
            mGameViewModel = mGameViewModel
            mScoreFragment = this@ScoreFragment
        }
    }

    fun playAgain(){
        mGameViewModel.resetGame()
        findNavController().navigate(
            ScoreFragmentDirections.actionScoreFragmentToPlayFragment()
        )
    }
}