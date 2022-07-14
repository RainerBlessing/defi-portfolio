package org.defichain.portfolio.walletinfo

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
class OceanAPICaller {
    private val client = OkHttpClient()

    @Serializable
    data class Data(val data: Aggregation)

    fun getAggregation(defiAddress: String): Aggregation {
        val request = Request.Builder()
            .url("https://ocean.defichain.com/v0/mainnet/address/$defiAddress/aggregation")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val stringResponse = response.body()!!.string()

            return Json.decodeFromString<Data>(stringResponse).data
        }
    }
}