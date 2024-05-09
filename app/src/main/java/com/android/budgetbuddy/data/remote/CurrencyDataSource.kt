package com.android.budgetbuddy.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* TODO: guardare le informazioni che vengono mandate dalla API
*   e schematizzarle qui*/

@Serializable
data class CurrenciesInfo(
    @SerialName("rates")
    val rates: Map<String, Double>
)


class RatesDataSource(
    private val httpClient: HttpClient
) {
    // private val baseUrl = "https://openexchangerates.org/api/latest.json"
   /* suspend fun getExchangeRates(query: String): List<CurrenciesInfo> {
        val url = "$baseUrl/?app_id=dbcad73d3ea4456284037b5890c372cc"
        return httpClient.get(url).body()
    }*/

    private val baseUrl = "http://10.0.2.2/test/data.json"
    //private val baseUrl = "https://www.google.com/"

    suspend fun getExchangeRates(): CurrenciesInfo {
        return httpClient.get(baseUrl).body()
    }
}