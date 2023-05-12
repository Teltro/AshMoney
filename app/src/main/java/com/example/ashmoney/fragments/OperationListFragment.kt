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
import com.example.ashmoney.utils.setItemDecoration
import com.example.ashmoney.viewmodels.OperationListViewModel
import kotlinx.coroutines.launch

class OperationListFragment : Fragment() {

    private val viewModel: OperationListViewModel.ViewModel by viewModels()

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
            val adapter = OperationListAdapter {
                val bundle = bundleOf("operationId" to it.id)
                navController.navigate(R.id.operationFragmentDestination, bundle)
            }

            val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            recyclerView.setItemDecoration(layoutManager.orientation)

            lifecycleScope.launch {
                viewModel.outputs.operationList().collect(adapter::submitList)
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