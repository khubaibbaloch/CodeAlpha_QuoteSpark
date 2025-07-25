package com.quotespark.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.quotespark.app.data.model.Quote
import com.quotespark.app.ui.theme.QuoteSparkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuoteSparkTheme {
                val context = LocalContext.current
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("QuoteSpark") },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                ) { innerPadding ->
                    QuoteScreen(paddingValues = innerPadding, context)
                }
            }
        }
    }
}

@Composable
fun QuoteScreen(paddingValues: PaddingValues, context: Context) {
    val quotes = remember { loadQuotesFromAssets(context) }
    var currentQuote by remember { mutableStateOf(getRandomQuote(quotes)) }

    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Quote Card
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {

                // Top-right Row with Icon Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {
                        val combinedText = "\"${currentQuote.quote}\"\n- ${currentQuote.author}"
                        val clip = ClipData.newPlainText("Quote", combinedText)
                        clipboardManager.setPrimaryClip(clip)
                        Toast.makeText(context, "Quote copied!", Toast.LENGTH_SHORT).show()
                    },
                        modifier = Modifier.size(30.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.outline_content_copy_24),
                            contentDescription = "Copy Quote",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = {
                        val combinedText = "\"${currentQuote.quote}\"\n- ${currentQuote.author}"
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, combinedText)
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "Share quote via")
                        context.startActivity(shareIntent)
                    }, modifier = Modifier.size(30.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_share_24),
                            contentDescription = "Share Quote",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Quote Text
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "“${currentQuote.quote}”",
                        fontSize = 18.sp,
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
        }

        // New Quote Button
        Button(onClick = {
            currentQuote = getRandomQuote(quotes)
        }) {
            Text("New Quote")
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
