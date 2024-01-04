package com.khattab.payoff.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khattab.payoff.data.model.Market
import com.khattab.payoff.databinding.ItemMarketBinding

class MarketAdapter(val context: Context) : RecyclerView.Adapter<LargeNewsViewHolder>() {

    private lateinit var dataBinding: ItemMarketBinding
    private var markets: ArrayList<Market> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LargeNewsViewHolder {
        dataBinding = ItemMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LargeNewsViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: LargeNewsViewHolder, position: Int) {
        val student = markets[position]
        holder.bind(student)
        holder.itemView.setOnClickListener {
//            val intent = Intent(context, StudentDetailsActivity::class.java)
//            intent.putExtra("id", student.id)
//            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return if (markets.isNotEmpty())
            markets.size
        else
            0
    }

    fun updateStudentList(newMarket: ArrayList<Market>) {
        markets.clear()
        markets.addAll(newMarket)
        notifyDataSetChanged()
    }
}

class LargeNewsViewHolder(private val dataBinding: ItemMarketBinding) :
    RecyclerView.ViewHolder(dataBinding.root) {
    fun bind(market: Market) {
        dataBinding.tvMarketName.text = market.name
        dataBinding.tvMarketNumber.text = market.number


    }
}