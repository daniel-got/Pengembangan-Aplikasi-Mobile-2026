package com.example.newsfeedsimulator

import NewsFeedSimulator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val simulator = remember { NewsFeedSimulator() }
        val readCount by simulator.readCount.collectAsState()
        val newsList = remember { mutableStateListOf<String>() }

        LaunchedEffect(Unit) {
            val targetCategory = "Tech"
            simulator.newsFeedStream()
                .filter { it.category == targetCategory }
                .map { news -> "[KATEGORI: ${news.category.uppercase()}] ${news.title}" }
                .onEach { println("Memproses berita: $it") }
                .catch { e -> println("Error: ${e.message}") }
                .collect { formattedTitle ->
                    val detailDeferred = async { simulator.fetchNewsDetails(1) }
                    val detail = detailDeferred.await()
                    
                    newsList.add("$formattedTitle\n$detail")
                    simulator.markAsRead()
                }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).windowInsetsPadding(WindowInsets.safeContent)
        ) {
            Text("Simulasi News Feed", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Berita Dibaca: $readCount", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(newsList) { news ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = news,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}