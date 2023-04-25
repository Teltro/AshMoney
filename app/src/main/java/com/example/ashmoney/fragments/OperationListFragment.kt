package com.example.ashmoney.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.R
import com.example.ashmoney.adapters.OperationListAdapter
import com.example.ashmoney.databinding.FragmentOperationListBinding
import com.example.ashmoney.itemDecorations.RadioItemDecoration
import com.example.ashmoney.viewmodels.OperationListViewModel
import kotlinx.coroutines.launch

class OperationListFragment : Fragment() {

    private val viewModel: OperationListViewModel by viewModels()

    private lateinit var binding: FragmentOperationListBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOperationListBinding.inflate(inflater, container, false)
        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOperationList()
        setupCreateButtonAction()
    }

    private fun setupOperationList() {
        binding.operationListRecyclerView.let { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val adapter = OperationListAdapter {
                val bundle = bundleOf("operationId" to it.id)
                navController.navigate(R.id.operationFragmentDestination, bundle)
            }
            recyclerView.adapter = adapter

            setupDefaultVerticalList(recyclerView, adapter)

            lifecycleScope.launch {
                viewModel.list.collect(adapter::submitList)
            }
        }
    }

    private fun setupDefaultVerticalList(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
    ) {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        setItemDecoration(recyclerView, layoutManager.orientation)
    }

    private fun setItemDecoration(recyclerView: RecyclerView, orientation: Int) {
        requireContext().run {
            val drawable = ContextCompat.getDrawable(this, R.drawable.item_decoration)
            drawable?.let {
                val itemDecoration = RadioItemDecoration(drawable, orientation)
                recyclerView.addItemDecoration(itemDecoration)
            }
        }
    }

    private fun setupCreateButtonAction() {
        binding.operationListCreateButton.setOnClickListener {
            val bundle = bundleOf("operationId" to -1)
            navController.navigate(R.id.operationFragmentDestination, bundle)
        }
    }
}