package com.paypay.assignment

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApiService {
    private val client = OkHttpClient()

    suspend fun fetchDataFromApi(param: String): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://query1.finance.yahoo.com/v8/finance/chart/$param")
                .build()

            client.newCall(request).execute().body?.string()
        }
    }
}
