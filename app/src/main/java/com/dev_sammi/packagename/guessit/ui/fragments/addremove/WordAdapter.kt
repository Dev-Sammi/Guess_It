package com.dev_sammi.packagename.guessit.ui.fragments.addremove

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev_sammi.packagename.guessit.databinding.OneWordListingBinding
import com.dev_sammi.packagename.guessit.model.Word

class WordAdapter() : ListAdapter<Word, WordAdapter.WordViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding =
            OneWordListingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val currentWord = getItem(position)
        holder.bind(currentWord)
    }

    inner class WordViewHolder(private val binding: OneWordListingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentWord: Word?) {
            currentWord?.let {
                binding.tvWord.text = currentWord.text
            }
        }
    }


    class DiffCallback() : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }
}