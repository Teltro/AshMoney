package com.example.ashmoney.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.ashmoney.R
import com.example.ashmoney.databinding.FragmentAccountBinding
import com.example.ashmoney.databinding.FragmentDashboardConfigBinding
import com.example.ashmoney.viewmodels.AccountViewModel
import com.example.ashmoney.viewmodels.DashboardConfigViewModel

class DashboardConfigFragment : Fragment() {

    private val viewModel: DashboardConfigViewModel.ViewModel by viewModels()

    private lateinit var binding: FragmentDashboardConfigBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardConfigBinding.inflate(inflater, container, false)
        navController = findNavController()

        //viewModel.start(arguments?.getInt(AccountFragment.ACCOUNT_ID_KEY))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAccountingTypeList()
    }

    private fun setupAccountingTypeList() {

    }
}