package com.dev_sammi.packagename.guessit.ui.fragments.dialogs.Bulk

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.AddInBulkDialogBinding

class BulkFragment: DialogFragment(R.layout.add_in_bulk_dialog) {
    private lateinit var binding: AddInBulkDialogBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddInBulkDialogBinding.bind(view)
    }

}