package com.bill.bizpilot

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

import java.util.concurrent.TimeUnit

object AiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    // NOTE: In a production app, do not hardcode your API key. 
    // Use BuildConfig or a secure backend proxy.
    private const val API_KEY = "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"

    fun ask(question: String, sales: Double, expenses: Double, onResult: (String) -> Unit) {

        val prompt = """
            You are Bizpilot Copilot, a high-end fintech strategist. 
            Financial Context:
            - Revenue: Ksh $sales
            - Operational Costs: Ksh $expenses
            - Current Margin: Ksh ${sales - expenses}
            
            Task: Analyze the business state and answer the user's query with actionable insights. 
            If the query is about stock (contained in context), provide inventory optimization tips.
            Be concise, professional, and encouraging. Use emojis to highlight key metrics.
            
            User Question: $question
        """.trimIndent()

        val json = JSONObject()
        json.put("model", "gpt-4o-mini")

        val messages = JSONArray()
        messages.put(JSONObject().apply {
            put("role", "user")
            put("content", prompt)
        })

        json.put("messages", messages)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $API_KEY")
            .post(
                RequestBody.create(
                    "application/json".toMediaType(),
                    json.toString()
                )
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string() ?: ""
                    val res = JSONObject(responseBody)
                    val reply = res.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    onResult(reply)
                } catch (e: Exception) {
                    onResult("Error: Could not parse AI response")
                }
            }
        })
    }
}
