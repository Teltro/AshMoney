package com.example.ashmoney.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ashmoney.R
import com.example.ashmoney.adapters.AccountListAdapter
import com.example.ashmoney.databinding.FragmentAccountListBinding
import com.example.ashmoney.viewmodels.AccountListViewModel
import kotlinx.coroutines.launch

class AccountListFragment : Fragment() {

    private val viewModel: AccountListViewModel.ViewModel by viewModels()
    private lateinit var binding: FragmentAccountListBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountListBinding.inflate(inflater, container, false)
        navController = findNavController()

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAccountList()
        setupCreateButtonAction()
    }

    private fun setupAccountList() {
        binding.accountListRecyclerView.let { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val accountAdapter = AccountListAdapter {
                val bundle = bundleOf("accountId" to it.id)
                navController.navigate(
                    R.id.action_accountsFragment_to_createAccountFragment,
                    bundle
                )
            }
            recyclerView.adapter = accountAdapter

            lifecycleScope.launch {
                viewModel.outputs.accountList().collect(accountAdapter::submitList)
            }

            //viewModel.list.observe(viewLifecycleOwner, accountAdapter::submitList)
        }
    }

    private fun setupCreateButtonAction() {
        binding.accountListCreateButton.setOnClickListener {
            val bundle = bundleOf("accountId" to -1)
            navController.navigate(R.id.action_accountsFragment_to_createAccountFragment, bundle)
        }
    }
}