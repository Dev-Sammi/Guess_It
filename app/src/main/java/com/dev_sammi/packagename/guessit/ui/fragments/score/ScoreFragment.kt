package com.dev_sammi.packagename.guessit.ui.fragments.score

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentScoreBinding
import com.dev_sammi.packagename.guessit.ui.fragments.game.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScoreFragment : Fragment(R.layout.fragment_score){
    private val mScoreViewModel: ScoreViewModel by viewModels()
    private val args : ScoreFragmentArgs by navArgs()
    private lateinit var binding : FragmentScoreBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScoreBinding.bind(view)

        val g = args.totalScore



        binding.apply {
            lifecycleOwner = this@ScoreFragment
            mViewModel = mScoreViewModel
            mScoreFragment = this@ScoreFragment
        }
    }

    fun playAgain(){
        findNavController().navigate(
            ScoreFragmentDirections.actionScoreFragmentToPlayFragment()
        )
    }
}