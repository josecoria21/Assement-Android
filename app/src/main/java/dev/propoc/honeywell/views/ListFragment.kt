package dev.propoc.honeywell.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dev.propoc.honeywell.R
import dev.propoc.honeywell.adapter.ItemAdapter
import dev.propoc.honeywell.databinding.FragmentListBinding
import dev.propoc.honeywell.viewmodel.ListViewModel

class ListFragment : Fragment(), ItemAdapter.ItemClickListener {

    private val viewModel: ListViewModel by activityViewModels()
    private val adapter by lazy { ItemAdapter() }
    lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        binding.addItemCta.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addNewItemFragment)
        }

        setUpRecyclerView()

        adapter.setItemClickListener(this)

        viewModel.itemsList.observe(viewLifecycleOwner, Observer {
            adapter.differ.submitList(it)
        })

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.rvItems.adapter = adapter
    }

    override fun onItemClicked(position: Int) {
        val action = ListFragmentDirections.actionListFragmentToEditItemFragment(position)
        findNavController().navigate(action)
    }

}