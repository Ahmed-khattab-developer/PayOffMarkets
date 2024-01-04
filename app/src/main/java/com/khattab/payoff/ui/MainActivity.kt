package com.khattab.payoff.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.khattab.payoff.R
import com.khattab.payoff.databinding.ActivityMainBinding
import com.khattab.payoff.ui.adapter.MarketAdapter
import com.khattab.payoff.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var dataBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var marketAdapter: MarketAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        dataBinding.addMarket.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddMarketActivity::class.java))
        }

        mainViewModel.getMarketData()
        marketAdapter = MarketAdapter(this)
        dataBinding.rvMarket.adapter = marketAdapter
        dataBinding.rvMarket.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainViewModel.markets.collect {
                    if (it.isLoading) {
                        dataBinding.progressBar.visibility = View.VISIBLE
                    }
                    if (it.error.isNotBlank()) {
                        dataBinding.progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_LONG).show()
                        Log.e("aaaaaaaaaaa", it.error)
                    }
                    it.data?.let { arrayListMarket ->
                        if (arrayListMarket.isEmpty()) {
                            dataBinding.noData.visibility = View.VISIBLE
                        } else {
                            dataBinding.noData.visibility = View.GONE
                        }
                        marketAdapter.updateStudentList(arrayListMarket)
                        Log.d("Listtttttttttt", it.toString())
                        dataBinding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
}