package com.dev_sammi.packagename.guessit.ui.fragments.play

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentPlayBinding
import com.dev_sammi.packagename.guessit.ui.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFragment() : Fragment() {
    private val viewModel: PlayViewModel by viewModels()
    private lateinit var binding: FragmentPlayBinding
    private lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = (activity as MainActivity)
        mainActivity.setSupportActionBar(binding.myToolBar)
        setHasOptionsMenu(true)
        binding.apply {
            lifecycleOwner = this@PlayFragment
            mViewModel = viewModel
            mPlayFragment = this@PlayFragment
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        hideSystemUI()
    }

    override fun onDetach() {
        super.onDetach()
        showSystemUI()
        mainActivity.supportActionBar?.show()
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.play_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_settings -> {
                findNavController().navigate(R.id.settingsFragmentDialog)
                 true
            }
            R.id.mi_how_to_play -> {
                 true
            }
            R.id.mi_add_more_words -> {
                findNavController().navigate(
                    PlayFragmentDirections.actionPlayFragmentToAddRemoveWordsFragment()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun goToGameFragment() {
        findNavController().navigate(
            PlayFragmentDirections.actionPlayFragmentToGameFragment()
        )
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(mainActivity.window, true)
        WindowInsetsControllerCompat(mainActivity.window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.navigationBars())
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