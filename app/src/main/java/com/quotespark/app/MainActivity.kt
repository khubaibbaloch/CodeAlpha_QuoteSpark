package com.quotespark.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.quotespark.app.data.model.Quote
import com.quotespark.app.ui.theme.QuoteSparkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuoteSparkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    QuoteScreen(context)
                }
            }
        }
    }
}
@Composable
fun QuoteScreen(context: Context) {
    val quotes = remember { loadQuotesFromAssets(context) }
    var currentQuote by remember { mutableStateOf(getRandomQuote(quotes)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "“${currentQuote.quote}”",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "- ${currentQuote.author}",
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Button(onClick = {
                currentQuote = getRandomQuote(quotes)
            }) {
                Text("New Quote")
            }
        }
    }
}




fun loadQuotesFromAssets(context: Context): List<Quote> {
    val json = context.assets.open("quotes.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val type = object : TypeToken<List<Quote>>() {}.type
    return gson.fromJson(json, type)
}
fun getRandomQuote(quotes: List<Quote>): Quote {
    return quotes.random()
}
