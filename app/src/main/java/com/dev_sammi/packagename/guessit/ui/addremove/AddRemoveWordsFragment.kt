package com.dev_sammi.packagename.guessit.ui.addremove

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev_sammi.packagename.guessit.onTextChangeListener
import com.dev_sammi.packagename.guessit.R
import com.dev_sammi.packagename.guessit.databinding.FragmentAddRemoveWordsBinding
import com.dev_sammi.packagename.guessit.hideKeyboard
import com.dev_sammi.packagename.guessit.ui.utils.exhaustive
import com.dev_sammi.packagename.guessit.ui.utils.safe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AddRemoveWordsFragment : Fragment() {

    private lateinit var binding: FragmentAddRemoveWordsBinding
    private val viewModel: AddEditViewModel by viewModels()
    private lateinit var wordAdapter: WordAdapter
    private lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_remove_words, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        wordAdapter = WordAdapter()

        viewModel.allWords.observe(viewLifecycleOwner) {
            wordAdapter.submitList(it)
        }

        binding.apply {
            lifecycleOwner = this@AddRemoveWordsFragment

            rvWordsList.apply {
                adapter = wordAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
            btAddWord.setOnClickListener {
                val newWord = etAddNewWord.text.toString()
                etAddNewWord.hideKeyboard()
                etAddNewWord.setText("")
                viewModel.checkNewWord(newWord)

            }

        }

        //observing the event channel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect(){event->
                when(event){
                    is AddEditViewModel.WordListEvent.ShowAddedWordMessage -> TODO()
                    is AddEditViewModel.WordListEvent.ShowDeletedWordMessage -> TODO()
                    is AddEditViewModel.WordListEvent.ShowEditedWordMessage -> TODO()
                    is AddEditViewModel.WordListEvent.ShowErrorMessage -> TODO()
                }.exhaustive

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_word_menu, menu)

        val textQuery = viewModel.searchQuery

        val searchItem = menu.findItem(R.id.mi_app_bar_search)
        searchView = searchItem.actionView as SearchView
        if (!textQuery.value.isNullOrEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(textQuery.value, false)
        }

        searchView.onTextChangeListener {
            viewModel.setWordQuery(it)
        }
    }






}