package com.dev_sammi.packagename.guessit.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentPlayBinding

class PlayFragment(): Fragment() {
    private lateinit var  binding:FragmentPlayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playButton.setOnClickListener {
            findNavController().navigate(
                PlayFragmentDirections.actionPlayFragmentToGameFragment()
            )
        }
    }
}