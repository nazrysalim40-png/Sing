package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.SongReleaseDatabase
import com.example.data.model.Song
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import com.example.data.repository.SongReleaseRepository
import com.example.ui.components.TrackMetadataFormData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: SongReleaseDatabase
  private lateinit var repository: SongReleaseRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, SongReleaseDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = SongReleaseRepository(database)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Song Release", appName)
  }

  @Test
  fun `track metadata form data correctly holds essential attributes`() {
    val splits = listOf(
      SongWriterSplit(
        contributorName = "Taylor Swift",
        role = "Primary Songwriter",
        percentage = 60.0,
        proAffiliation = "BMI",
        ipiNumber = "123456789"
      ),
      SongWriterSplit(
        contributorName = "Jack Antonoff",
        role = "Producer / Composer",
        percentage = 40.0,
        proAffiliation = "ASCAP",
        ipiNumber = "987654321"
      )
    )

    val formData = TrackMetadataFormData(
      title = "Midnight Echoes",
      isrcCode = "US-AST-26-10492",
      releaseDateMillis = 1788000000000L,
      songwriterCredits = splits
    )

    assertEquals("Midnight Echoes", formData.title)
    assertEquals("US-AST-26-10492", formData.isrcCode)
    assertEquals(2, formData.songwriterCredits.size)
    val totalSplit = formData.songwriterCredits.sumOf { it.percentage }
    assertEquals(100.0, totalSplit, 0.001)
    assertTrue(totalSplit == 100.0)
  }

  @Test
  fun `room database persistently stores song entity metadata from release form`() = runBlocking {
    val song = Song(
      title = "Starlight Horizon",
      isrcCode = "US-S1Z-26-58392",
      releaseDateMillis = 1789000000000L,
      artistName = "Astraea",
      genre = "Synthwave",
      bpm = 128,
      musicalKey = "A Minor",
      distributor = "DistroKid",
      songwriterCreditsJson = """[{"name":"Astraea","role":"Lyricist","percentage":50.0,"pro":"BMI","ipi":"123"}]"""
    )

    val songId = database.songDao().insertSong(song)
    assertTrue(songId > 0)

    val retrieved = database.songDao().getSongByIdDirect(songId)
    assertNotNull(retrieved)
    assertEquals("Starlight Horizon", retrieved?.title)
    assertEquals("US-S1Z-26-58392", retrieved?.isrcCode)
    assertEquals(1789000000000L, retrieved?.releaseDateMillis)
    assertEquals("Astraea", retrieved?.artistName)
    assertEquals(128, retrieved?.bpm)
  }

  @Test
  fun `repository syncs song entity when saving track metadata form`() = runBlocking {
    val releaseId = repository.createReleaseWithDefaults(
      title = "Neon Nights",
      artistName = "Astraea"
    )

    val splits = listOf(
      SongWriterSplit(
        contributorName = "Astraea",
        role = "Topliner / Lyricist",
        percentage = 70.0,
        proAffiliation = "BMI",
        ipiNumber = "111222333"
      ),
      SongWriterSplit(
        contributorName = "Nova Beats",
        role = "Composer",
        percentage = 30.0,
        proAffiliation = "ASCAP",
        ipiNumber = "444555666"
      )
    )

    repository.updateEssentialMetadataAndSplits(
      releaseId = releaseId,
      title = "Neon Nights (VIP Mix)",
      isrcCode = "US-AST-26-99999",
      releaseDateMillis = 1790000000000L,
      splits = splits
    )

    val song = repository.getSongByReleaseIdDirect(releaseId)
    assertNotNull(song)
    assertEquals("Neon Nights (VIP Mix)", song?.title)
    assertEquals("US-AST-26-99999", song?.isrcCode)
    assertEquals(1790000000000L, song?.releaseDateMillis)
    assertTrue(song?.songwriterCreditsJson?.contains("Nova Beats") == true)
  }

  @Test
  fun `room database queries all saved songs and lists title and release date for each`() = runBlocking {
    val song1 = Song(
      title = "Cyber Odyssey",
      releaseDateMillis = 1789500000000L,
      artistName = "Astraea",
      genre = "Synthwave"
    )
    val song2 = Song(
      title = "Velvet Skyline",
      releaseDateMillis = 1792000000000L,
      artistName = "Astraea",
      genre = "Dream Pop"
    )

    repository.insertSong(song1)
    repository.insertSong(song2)

    val allSavedSongs = repository.allSongs.first()
    assertTrue(allSavedSongs.size >= 2)

    val titles = allSavedSongs.map { it.title }
    val releaseDates = allSavedSongs.map { it.releaseDateMillis }

    assertTrue(titles.contains("Cyber Odyssey"))
    assertTrue(titles.contains("Velvet Skyline"))
    assertTrue(releaseDates.contains(1789500000000L))
    assertTrue(releaseDates.contains(1792000000000L))

    // Verify each song has valid title and release date
    allSavedSongs.forEach { song ->
      assertTrue(song.title.isNotBlank())
      assertTrue(song.releaseDateMillis > 0L)
    }
  }

  @Test
  fun `filtering songs by title correctly matches search query`() = runBlocking {
    val songs = listOf(
      Song(title = "Midnight Echoes", releaseDateMillis = 1788000000000L),
      Song(title = "Echoes in the Rain", releaseDateMillis = 1789000000000L),
      Song(title = "Solar Flare", releaseDateMillis = 1790000000000L)
    )

    val query = "Echoes"
    val filtered = songs.filter { it.title.contains(query, ignoreCase = true) }

    assertEquals(2, filtered.size)
    assertTrue(filtered.any { it.title == "Midnight Echoes" })
    assertTrue(filtered.any { it.title == "Echoes in the Rain" })
    assertTrue(filtered.none { it.title == "Solar Flare" })
  }

  @Test
  fun `MetadataExportUtils generates correct CSV and share sheet format`() {
    val csv = com.example.util.MetadataExportUtils.exportSongToCsv(
      title = "Midnight Echoes",
      artist = "Astraea",
      featuredArtists = "Luna",
      isrc = "US-A12-26-00101",
      upc = "198000123456",
      releaseDateMillis = 1788000000000L,
      genre = "Electronic",
      subGenre = "Synthwave",
      bpm = 124,
      musicalKey = "D Minor",
      explicit = false,
      distributor = "DistroKid",
      pitchBlurb = "An atmospheric synthwave single.",
      splitsSummary = "Astraea (Primary 70%); Luna (Featured 30%)"
    )

    assertTrue(csv.contains("Track Title,Primary Artist,Featured Artists"))
    assertTrue(csv.contains("Midnight Echoes"))
    assertTrue(csv.contains("US-A12-26-00101"))
    assertTrue(csv.contains("124"))
    assertTrue(csv.contains("CLEAN"))

    val shareSheet = com.example.util.MetadataExportUtils.generateDistributorShareSheet(
      title = "Midnight Echoes",
      artist = "Astraea",
      featuredArtists = "Luna",
      isrc = "US-A12-26-00101",
      upc = "198000123456",
      releaseDateMillis = 1788000000000L,
      genre = "Electronic",
      subGenre = "Synthwave",
      bpm = 124,
      musicalKey = "D Minor",
      explicit = false,
      distributor = "DistroKid"
    )

    assertTrue(shareSheet.contains("TRACK TITLE: Midnight Echoes"))
    assertTrue(shareSheet.contains("PRIMARY ARTIST: Astraea"))
    assertTrue(shareSheet.contains("ISRC Code: US-A12-26-00101"))
    assertTrue(shareSheet.contains("BPM / Tempo: 124 BPM"))
  }

  @Test
  fun `SongWriterSplit calculation and equal split balancing`() {
    val splits = listOf(
      SongWriterSplit(contributorName = "Artist A", role = "Songwriter", percentage = 50.0, proAffiliation = "BMI", ipiNumber = "12345"),
      SongWriterSplit(contributorName = "Producer B", role = "Producer", percentage = 50.0, proAffiliation = "ASCAP", ipiNumber = "67890")
    )

    val totalPct = splits.sumOf { it.percentage }
    assertEquals(100.0, totalPct, 0.001)

    // Test 3 collaborators equal split
    val threeCollaborators = listOf(
      SongWriterSplit(contributorName = "Artist A", role = "Songwriter", percentage = 0.0),
      SongWriterSplit(contributorName = "Producer B", role = "Producer", percentage = 0.0),
      SongWriterSplit(contributorName = "Co-Writer C", role = "Lyricist", percentage = 0.0)
    )

    val count = threeCollaborators.size
    val basePct = java.math.BigDecimal(100.0 / count).setScale(2, java.math.RoundingMode.DOWN).toDouble()
    val remainder = java.math.BigDecimal(100.0 - (basePct * count)).setScale(2, java.math.RoundingMode.HALF_UP).toDouble()

    val balanced = threeCollaborators.mapIndexed { idx, item ->
      val extra = if (idx == 0) remainder else 0.0
      item.copy(percentage = java.math.BigDecimal(basePct + extra).setScale(2, java.math.RoundingMode.HALF_UP).toDouble())
    }

    val balancedTotal = balanced.sumOf { it.percentage }
    assertEquals(100.0, balancedTotal, 0.001)
    assertEquals(33.34, balanced[0].percentage, 0.001)
    assertEquals(33.33, balanced[1].percentage, 0.001)
    assertEquals(33.33, balanced[2].percentage, 0.001)
  }

  @Test
  fun `Create new release with title, artist, and distribution date and persist to database`() = runBlocking {
    val releaseDate = 1788000000000L
    val newId = repository.createReleaseWithDefaults(
      title = "Quantum Solitude",
      artistName = "Astraea",
      featuredArtists = "Nova",
      genre = "Electronic",
      subGenre = "Ambient Synth",
      releaseDateMillis = releaseDate,
      bpm = 118,
      musicalKey = "A Minor",
      distributor = "DistroKid",
      coverArtPreset = "neon",
      pitchBlurb = "An ambient synth journey exploring cosmic depths."
    )

    assertTrue(newId > 0)
    val saved = repository.getReleaseById(newId).first()
    assertNotNull(saved)
    assertEquals("Quantum Solitude", saved?.title)
    assertEquals("Astraea", saved?.artistName)
    assertEquals("Nova", saved?.featuredArtists)
    assertEquals(releaseDate, saved?.releaseDateMillis)
    assertEquals("Electronic", saved?.genre)
    assertEquals("DistroKid", saved?.distributor)
  }

  @Test
  fun `Monthly release frequency aggregation by month and year`() {
    val cal = Calendar.getInstance()
    cal.set(2026, Calendar.JANUARY, 15, 0, 0, 0)
    val janDate = cal.timeInMillis

    cal.set(2026, Calendar.MARCH, 10, 0, 0, 0)
    val marDate1 = cal.timeInMillis

    cal.set(2026, Calendar.MARCH, 25, 0, 0, 0)
    val marDate2 = cal.timeInMillis

    cal.set(2026, Calendar.AUGUST, 1, 0, 0, 0)
    val augDate = cal.timeInMillis

    cal.set(2025, Calendar.DECEMBER, 12, 0, 0, 0)
    val prevYearDate = cal.timeInMillis

    val testSongs = listOf(
      Song(id = 1, title = "Jan Single", artistName = "Astraea", releaseDateMillis = janDate),
      Song(id = 2, title = "March Anthem", artistName = "Astraea", releaseDateMillis = marDate1),
      Song(id = 3, title = "March Remix", artistName = "Astraea", releaseDateMillis = marDate2),
      Song(id = 4, title = "August Drop", artistName = "Astraea", releaseDateMillis = augDate),
      Song(id = 5, title = "Old 2025 Song", artistName = "Astraea", releaseDateMillis = prevYearDate)
    )

    // Aggregate for 2026
    val selectedYear = 2026
    val monthCounts = IntArray(12)
    testSongs.forEach { s ->
      cal.timeInMillis = s.releaseDateMillis
      if (cal.get(Calendar.YEAR) == selectedYear) {
        monthCounts[cal.get(Calendar.MONTH)]++
      }
    }

    assertEquals(1, monthCounts[Calendar.JANUARY])
    assertEquals(0, monthCounts[Calendar.FEBRUARY])
    assertEquals(2, monthCounts[Calendar.MARCH])
    assertEquals(1, monthCounts[Calendar.AUGUST])
    assertEquals(0, monthCounts[Calendar.DECEMBER])

    val total2026 = monthCounts.sum()
    assertEquals(4, total2026)
  }

  @Test
  fun `multi-genre categorizes release properly in Room and parses list`() = runBlocking {
    val multiGenreRelease = SongRelease(
      title = "Cyber Neon Nights",
      artistName = "Astraea",
      genre = "Electronic, Synthwave, Indie Pop",
      subGenre = "Retrowave",
      bpm = 124,
      musicalKey = "F# Minor",
      distributor = "DistroKid"
    )

    val id = database.songReleaseDao().insertRelease(multiGenreRelease)
    assertTrue(id > 0)

    val fetched = repository.getReleaseById(id).first()
    assertNotNull(fetched)
    assertEquals("Electronic, Synthwave, Indie Pop", fetched?.genre)
    val genres = fetched?.genreList
    assertNotNull(genres)
    assertEquals(3, genres?.size)
    assertTrue(genres?.contains("Electronic") == true)
    assertTrue(genres?.contains("Synthwave") == true)
    assertTrue(genres?.contains("Indie Pop") == true)
  }

  @Test
  fun `song release with light mood cover art preset persists and retrieves correctly`() = runBlocking {
    val lightRelease = SongRelease(
      title = "Summer Sunburst",
      artistName = "Solara",
      genre = "Indie Pop",
      coverArtPreset = "light"
    )

    val id = database.songReleaseDao().insertRelease(lightRelease)
    val fetched = repository.getReleaseById(id).first()
    assertNotNull(fetched)
    assertEquals("light", fetched?.coverArtPreset)
    assertEquals("Summer Sunburst", fetched?.title)
  }
}
