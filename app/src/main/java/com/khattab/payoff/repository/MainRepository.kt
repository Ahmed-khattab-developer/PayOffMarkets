package com.khattab.payoff.repository

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.khattab.payoff.data.model.City
import com.khattab.payoff.data.model.Market
import com.khattab.payoff.utils.Resource
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor() {

    private val fireStoreDatabase = FirebaseFirestore.getInstance()
    private val cityModel = "city"
    private val marketModel = "market"

    suspend fun getCityData() = flow {
        emit(Resource.Loading())
        try {
            val snapshot = fireStoreDatabase.collection(cityModel).get().await()
            val list: ArrayList<City> = ArrayList()
            for (document in snapshot.documents) {
                val city = document.toObject(City::class.java)
                list.add(city!!)
            }
            emit(Resource.Success(list))

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(
                Resource.Error(e.localizedMessage ?: "Check Your Internet Connection")
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: ""))
        }
    }

    suspend fun addMarket(market: Market, id: String) = flow {
        emit(Resource.Loading())
        try {
            fireStoreDatabase.collection(marketModel)
                .document(id).set(market).await()
            emit(Resource.Success("success"))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(
                Resource.Error(e.localizedMessage ?: "Check Your Internet Connection")
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: ""))
        }
    }

    suspend fun getMarketData() = flow {
        emit(Resource.Loading())
        try {
            val snapshot = fireStoreDatabase.collection(marketModel)
                .orderBy("name").get().await()
            val list: ArrayList<Market> = ArrayList()
            for (document in snapshot.documents) {
                val market: Market? = document.toObject(Market::class.java)
                list.add(market!!)
            }
            emit(Resource.Success(list))

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown Error"))
        } catch (e: IOException) {
            emit(
                Resource.Error(e.localizedMessage ?: "Check Your Internet Connection")
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: ""))
        }
    }
}