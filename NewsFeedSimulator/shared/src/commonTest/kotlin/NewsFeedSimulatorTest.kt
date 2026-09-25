import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NewsFeedSimulatorTest {

    @Test
    fun testMarkAsReadUpdatesReadCount() = runTest {
        val simulator = NewsFeedSimulator()

        assertEquals(0, simulator.readCount.value)

        // mark as read 2x
        simulator.markAsRead()
        simulator.markAsRead()

        // total menjadi 2 yang telah dibaca
        assertEquals(2, simulator.readCount.value)
    }

    @Test
    fun testNewsFeedStreamEmitsItems() = runTest {
        val simulator = NewsFeedSimulator()
        
        // Collect emitted items (will use test dispatcher's virtual time to skip delays)
        val emittedNews = mutableListOf<NewsItem>()
        simulator.newsFeedStream().collect {
            emittedNews.add(it)
        }
        
        // Ensure that the dummy data (5 items) were emitted
        assertEquals(5, emittedNews.size)
        assertEquals("Kotlin Multiplatform Makin Populer", emittedNews[0].title)
        assertEquals("Tech", emittedNews[0].category)
    }

    @Test
    fun testFetchNewsDetails() = runTest {
        val simulator = NewsFeedSimulator()
        
        val details = simulator.fetchNewsDetails(1)
        assertEquals("Detail lengkap untuk artikel #1: Berita ini disajikan secara lengkap...", details)
    }

    @Test
    fun testRunMainSimulationToTerminal() {
        main()
    }
}
