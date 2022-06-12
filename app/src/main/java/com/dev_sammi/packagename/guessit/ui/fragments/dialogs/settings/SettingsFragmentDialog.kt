package com.dev_sammi.packagename.guessit.ui.fragments.dialogs.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.datastore.preferences.protobuf.StringValue
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentSettingDialogBinding
import com.dev_sammi.packagename.guessit.ui.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "SettingsFragmentDialog"

@AndroidEntryPoint
class SettingsFragmentDialog : DialogFragment() {
    private lateinit var binding: FragmentSettingDialogBinding
    private lateinit var settingsFragmentDialog: SettingsFragmentDialog
    private val viewModel: SettingsFragmentViewModel by viewModels()
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_setting_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.startModel()
        mainActivity = activity as MainActivity



        binding.apply {
            mViewModel = viewModel
            lifecycleOwner = this@SettingsFragmentDialog
            mSettingsDialogFragment = this@SettingsFragmentDialog
            etGameDuration.setText("${viewModel.gameDuration.value}")


            etGameDuration.addTextChangedListener {
                if (it.toString() != "") {
                    viewModel.saveGameDuration(Integer.parseInt(it.toString()))
                }
            }


        }
    }


    fun goToAddRemoveFragment() {
        findNavController().navigate(SettingsFragmentDialogDirections.actionSettingsFragmentDialogToAddRemoveWordsFragment())
        dialog?.dismiss()
    }


}