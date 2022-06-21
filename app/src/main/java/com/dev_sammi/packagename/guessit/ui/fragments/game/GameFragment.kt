package com.dev_sammi.packagename.guessit.ui.fragments.game

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentGameBinding
import com.dev_sammi.packagename.guessit.ui.activities.MainActivity
import com.dev_sammi.packagename.guessit.ui.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "GameFragment"

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {
    private lateinit var binding: FragmentGameBinding
    private val mGameViewModel: GameViewModel by viewModels()
    private lateinit var mainActivity: MainActivity

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
        mainActivity = (activity as MainActivity)

        viewLifecycleOwner.lifecycleScope.launch() {
            mGameViewModel.dataStoreValues.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect() {
                    mGameViewModel.setDataStoreValues(it)
                }
        }

        if (mGameViewModel._isGameAlreadyRun) {
            hideTimerCardView()
        } else {
            mGameViewModel.allWordsListForGame.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    mGameViewModel.sortAndSelectTenWords(it)
                    playGame()
                }
            }
            mGameViewModel.startGetReadyTimer()
        }


//        mGameViewModel.buzzPattern.observe(viewLifecycleOwner) { vibePattern ->
////            vibrate(vibePattern.buzzPattern)
//        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mGameViewModel.buzzEvent.collectLatest {
                    vibrate(it.buzzPattern)
                }
            }
        }






        binding.apply {
            mViewModel = mGameViewModel
            lifecycleOwner = this@GameFragment
            mGameFragment = this@GameFragment
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            whenStarted {
                mGameViewModel.eventChannel.collect() { event ->
                    when (event) {
                        GameViewModel.GameEvents.FinishStartingCountDown -> {
                            withContext(Dispatchers.Main) {
                                binding.startingTimerLayout.isVisible = false
                                binding.btCorrect.isEnabled = true
                                binding.btSkipWord.isEnabled = true
                                mGameViewModel.startMainGameTimer(false)
                            }
                        }
                        GameViewModel.GameEvents.NavigateToScoreFragment -> {
                            findNavController().navigate(
                                GameFragmentDirections.actionGameFragmentToScoreFragment(
                                    mGameViewModel.score.value!!,
                                    mGameViewModel.takeNumOfWord.value!!
                                )
                            )
                        }
                    }.exhaustive
                }
            }
        }

    }


    fun cancelGame() {
        findNavController().navigate(
            GameFragmentDirections.actionGameFragmentToPlayFragment()
        )
    }

    private fun playGame() {
        binding.apply {
            pgbProgressBarId.isVisible = false
            startingTimerLayout.isVisible = true
        }
    }

    private fun hideTimerCardView() {
        binding.apply {
            startingTimerLayout.isVisible = false
            pgbProgressBarId.isVisible = false
            btCorrect.isEnabled = true
            btSkipWord.isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        hideSystemUI()
        mainActivity.supportActionBar?.hide()
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

    private fun vibrate(buzzPattern: LongArray) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            val vibrator = activity?.getSystemService<Vibrator>()
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(buzzPattern, -1))
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(buzzPattern, -1)
                }
            }
        }
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


