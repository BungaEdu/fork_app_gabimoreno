package soy.gabimoreno.data.remote.datasource.podcast

import arrow.core.right
import com.prof.rssparser.Parser
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.Before
import org.junit.Test
import soy.gabimoreno.core.testing.coVerifyOnce
import soy.gabimoreno.data.remote.mapper.toDomain
import soy.gabimoreno.fake.buildChannel

@ExperimentalCoroutinesApi
class RemotePodcastDatasourceTest {

    private val rssParser: Parser = mockk()
    private lateinit var datasource: RemotePodcastDatasource

    @Before
    fun setUp() {
        datasource = RemotePodcastDatasource(
            rssParser
        )
    }

    @Test
    fun `GIVEN a success response WHEN getEpisodes THEN get the expected EpisodesWrapper`() =
        runTest {
            val url = "url"
            val channel = buildChannel(url)
            val episodesWrapper = channel.toDomain()
            coEvery { rssParser.getChannel(url) } returns channel

            val result = datasource.getEpisodes(url)

            result shouldBeEqualTo episodesWrapper.right()
            coVerifyOnce {
                rssParser.getChannel(url)
            }
        }

    @Test
    fun `GIVEN a failure response WHEN getEpisodes THEN get the error`() =
        runTest {
            val url = "url"
            coEvery { rssParser.getChannel(url) } throws Throwable()

            val result = datasource.getEpisodes(url)

            result.isLeft().shouldBeTrue()
            coVerifyOnce {
                rssParser.getChannel(url)
            }
        }
}
