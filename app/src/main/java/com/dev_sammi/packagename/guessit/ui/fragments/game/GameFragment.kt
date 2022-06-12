package com.dev_sammi.packagename.guessit.ui.fragments.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentGameBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "GameFragment"

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {
    private lateinit var binding: FragmentGameBinding
    private val mGameViewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*this get and observe data from the database through the gameViewModel and send the data to
        be sorted and ten taken.*/
        mGameViewModel.allWordsListForGame.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                mGameViewModel.sortAndSelectTenWords(it)
                showGame()
            }
        }

        binding.apply {
            mViewModel = mGameViewModel
            lifecycleOwner = this@GameFragment
        }

    }

    private fun showGame() {
        binding.pgbProgressBarId.isVisible = false
        binding.clGameDisplayId.isVisible = true
    }


}


/*    private lateinit var binding: FragmentGameBinding
    private val viewModel: GameViewModel by viewModels()
    private lateinit var mainActivity: MainActivity



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        mainActivity.supportActionBar?.hide()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = (activity as MainActivity)






//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        viewModel.getNextWord()


        binding.apply {
            mViewModel = viewModel
            lifecycleOwner = this@GameFragment
            btSkipWord.setOnClickListener {
                viewModel.skipAnswer()
            }

            btCorrect.setOnClickListener {
                viewModel.correctAnswer()
            }

        }
        viewModel.timer.start()


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect() { event ->
                when (event) {
                    GameViewModel.GameEvents.FinishCountDown -> {
                        binding.cvGameCardView.isVisible = false
                        viewModel.timer2.start()
                    }
                }.exhaustive

            }
        }
        viewModel.buzzPattern.observe(viewLifecycleOwner){
            vibrate(it.buzzPattern)
        }
    }

    private fun vibrate(pattern: LongArray) {
        val vibrator = activity?.getSystemService<Vibrator>()
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                vibrator.vibrate(pattern, -1)
            }
        }
    }



    override fun onDetach() {
        super.onDetach()
        showSystemUI()
        mainActivity.supportActionBar?.show()
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(mainActivity.window, false)
        WindowInsetsControllerCompat(mainActivity.window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(mainActivity.window, true)
        WindowInsetsControllerCompat(mainActivity.window, binding.root).let { controller ->
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

}*/


