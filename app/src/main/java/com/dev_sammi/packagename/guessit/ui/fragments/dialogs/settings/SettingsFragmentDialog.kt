package com.dev_sammi.packagename.guessit.ui.fragments.dialogs.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentSettingDialogBinding
import com.dev_sammi.packagename.guessit.ui.activities.MainActivity
import com.dev_sammi.packagename.guessit.ui.utils.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "SettingsFragmentDialog"

@AndroidEntryPoint
class SettingsFragmentDialog : DialogFragment() {
    private lateinit var binding: FragmentSettingDialogBinding
    private lateinit var settingsFragmentDialog: SettingsFragmentDialog
    private val settingsViewModel: SettingsFragmentViewModel by viewModels()
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
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
            repeatOnLifecycle(Lifecycle.State.STARTED){
                settingsViewModel.dataStoreValues.collect(){storedSetting ->
                    withContext(Dispatchers.Main) {
                        settingsViewModel.restoreValues(storedSetting)
                    }
                }
            }
        }
        settingsViewModel.allWordsListForGame.observe(viewLifecycleOwner){
            settingsViewModel.setNumOfAllWordsInDatabase(it.size)
        }


        binding.apply {
            mViewModel = settingsViewModel
            lifecycleOwner = this@SettingsFragmentDialog
            mSettingsDialogFragment = this@SettingsFragmentDialog
           /* etGameDuration.setText("${viewModel.gameDuration.value}")


            etGameDuration.addTextChangedListener {
                if (it.toString() != "") {
                    viewModel.saveGameDuration(Integer.parseInt(it.toString()))
                }
            }*/

//            etDurationHours.addTextChangedListener {
//                if (it.toString() != "") {
//                    settingsViewModel.saveHour(Integer.parseInt(it.toString()))
//                }
//            }
//            etDurationSeconds.addTextChangedListener {
//                if (it.toString() != "") {
//                    settingsViewModel.saveSecond(Integer.parseInt(it.toString()))
//                }
//            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            whenStarted {
                settingsViewModel.settingsEvents.collect(){event ->
                    when(event){
                        is SettingsFragmentViewModel.SettingValidator.InvalidInput -> {
                            Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                        }
                        is SettingsFragmentViewModel.SettingValidator.ValidInput -> {
                            Snackbar.make(requireView(),"Settings saved successfully!", Snackbar.LENGTH_SHORT).show()
                        }
                    }.exhaustive

                }
            }
        }


    }

    fun resetSettings(){
        settingsViewModel.checkInputs(0,0,10,10)
    }


    fun goToAddRemoveFragment() {
        saveAllSettings()
        findNavController().navigate(SettingsFragmentDialogDirections.actionSettingsFragmentDialogToAddRemoveWordsFragment())
        dialog?.dismiss()
    }

    fun saveAllSettings(){
        val hrs = Integer.parseInt(binding.etDurationHours.text.toString())
        val mins = Integer.parseInt(binding.etDurationMinutes.text.toString())
        val secs = Integer.parseInt(binding.etDurationSeconds.text.toString())
        val numOfWord = Integer.parseInt(binding.etNumberOfWordsPerGame.text.toString())
        settingsViewModel.checkInputs(hrs,mins,secs,numOfWord)
//        if (correctDurationInput){
//            Snackbar.make(requireView(),"Good input", Snackbar.LENGTH_SHORT).show()
//        }else{Snackbar.make(requireView(), "Invalid duration input", Snackbar.LENGTH_SHORT).show()}

    }


}