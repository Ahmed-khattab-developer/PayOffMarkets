package com.khattab.payoff.utils.appUtils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.khattab.payoff.R
import com.khattab.payoff.databinding.NoInternetDialogBinding

class NoInternetDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: NoInternetDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.no_internet_dialog, null, false
        )
        setContentView(binding.root)
    }

}