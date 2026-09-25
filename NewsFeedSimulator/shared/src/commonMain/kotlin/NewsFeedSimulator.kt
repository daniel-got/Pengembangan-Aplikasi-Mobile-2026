import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Clock.System

// model
data class NewsItem(
    val id: Int,
    val title: String,
    val category: String
)
class NewsFeedSimulator {
    // StateFlow untuk menyimpan jumlah berita yang sudah dibaca
    private val _readCount = MutableStateFlow(0)
    val readCount: StateFlow<Int> = _readCount.asStateFlow()

    // Flow yang menghasilkan berita setiap 2 detik
    fun newsFeedStream(): Flow<NewsItem> = flow {
        val dummyNews = listOf(
            NewsItem(1, "Kotlin Multiplatform Makin Populer", "Tech"),
            NewsItem(2, "Hasil Pertandingan Liga Champions Tadi Malam", "Sports"),
            NewsItem(3, "Update Terbaru Fitur Coroutines & Flow", "Tech"),
            NewsItem(4, "Resep Makanan Sehat untuk Sarapan", "Lifestyle"),
            NewsItem(5, "AI Mengubah Cara Kerja Software Engineering", "Tech")
        )

        for (news in dummyNews) {
            delay(2000L) // Emit setiap 2 detik
            emit(news)
        }
    }

    // Fungsi menandai berita telah dibaca
    fun markAsRead() {
        _readCount.value++ //
    }

    // Suspend function untuk mengambil detail berita secara asynchronous
    suspend fun fetchNewsDetails(newsId: Int): String = withContext(Dispatchers.Default) {
        delay(500L)
        "Detail lengkap untuk artikel #$newsId: Berita ini disajikan secara lengkap..."
    }
}

fun main() = runBlocking {
    val simulator = NewsFeedSimulator()
    val targetCategory = "Tech"

    println("=== SIMULATOR NEWS FEED DIMULAI ===")

    val trackerJob = launch {
        simulator.readCount.collect { count ->
            println("[StateFlow Update] Total Berita Dibaca: $count") //
        }
    }

    // 2 & 3. Flow Pipeline: Filter, Map, dan Catch
    simulator.newsFeedStream()
        .filter { it.category == targetCategory } // Filter kategori (tech)
        .map { news -> "[KATEGORI: ${news.category.uppercase()}] ${news.title}" } // Transformasi format news
        .onEach { println("Memproses item: $it") } // Operator onEach (Efek samping)
        .catch { e -> println("Error saat memproses stream berita: ${e.message}") } // Error handling
        .collect { formattedTitle ->
            println("\nBerita Baru Masuk -> $formattedTitle")

            // Async/Await untuk detail berita
            val detailDeferred = async { simulator.fetchNewsDetails(1) }
            val detail = detailDeferred.await() //
            println("-> $detail")

            // Update StateFlow
            simulator.markAsRead()
        }
    trackerJob.cancel()
    println("\n=== SIMULATOR SELESAI ===")
}