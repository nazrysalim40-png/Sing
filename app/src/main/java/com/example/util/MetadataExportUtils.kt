package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.model.Song
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility for exporting song metadata to CSV format and distributor share sheet text,
 * helping independent artists prepare deliverables for DistroKid, TuneCore, CD Baby,
 * DSP Pitching, and Publishing Administrations.
 */
object MetadataExportUtils {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    /**
     * Converts a single Song or SongRelease and its splits into standard CSV format.
     */
    fun exportSongToCsv(
        title: String,
        artist: String,
        featuredArtists: String = "",
        isrc: String,
        upc: String = "",
        releaseDateMillis: Long,
        genre: String,
        subGenre: String = "",
        bpm: Int,
        musicalKey: String,
        explicit: Boolean,
        distributor: String,
        lyrics: String = "",
        pitchBlurb: String = "",
        splitsSummary: String = ""
    ): String {
        val headers = listOf(
            "Track Title",
            "Primary Artist",
            "Featured Artists",
            "ISRC Code",
            "UPC Code",
            "Release Date",
            "Primary Genre",
            "Sub-Genre",
            "BPM",
            "Key",
            "Explicit Lyrics",
            "Distributor",
            "Songwriters & Splits",
            "Pitch Blurb"
        ).joinToString(",") { escapeCsv(it) }

        val row = listOf(
            title,
            artist,
            featuredArtists,
            isrc,
            upc,
            dateFormatter.format(Date(releaseDateMillis)),
            genre,
            subGenre,
            bpm.toString(),
            musicalKey,
            if (explicit) "EXPLICIT" else "CLEAN",
            distributor,
            splitsSummary,
            pitchBlurb
        ).joinToString(",") { escapeCsv(it) }

        return "$headers\n$row"
    }

    /**
     * Converts a list of songs into a complete CSV file format for distributor catalogs.
     */
    fun exportSongsListToCsv(songs: List<Song>): String {
        val headers = listOf(
            "ID",
            "Track Title",
            "Primary Artist",
            "ISRC Code",
            "UPC Code",
            "Release Date (YYYY-MM-DD)",
            "Genre",
            "Sub-Genre",
            "BPM",
            "Key",
            "Explicit",
            "Distributor",
            "Songwriters JSON / Splits",
            "Curator Pitch Blurb"
        ).joinToString(",") { escapeCsv(it) }

        val rows = songs.joinToString("\n") { song ->
            listOf(
                song.id.toString(),
                song.title,
                song.artistName,
                song.isrcCode,
                song.upcCode,
                dateFormatter.format(Date(song.releaseDateMillis)),
                song.genre,
                song.subGenre,
                song.bpm.toString(),
                song.musicalKey,
                if (song.explicitLyrics) "EXPLICIT" else "CLEAN",
                song.distributor,
                song.songwriterCreditsJson,
                song.pitchBlurb
            ).joinToString(",") { escapeCsv(it) }
        }

        return "$headers\n$rows"
    }

    /**
     * Formats a human-readable distributor share sheet text ready for clipboard or sharing.
     */
    fun generateDistributorShareSheet(
        title: String,
        artist: String,
        featuredArtists: String = "",
        isrc: String,
        upc: String = "",
        releaseDateMillis: Long,
        genre: String,
        subGenre: String = "",
        bpm: Int,
        musicalKey: String,
        explicit: Boolean,
        distributor: String,
        splits: List<SongWriterSplit> = emptyList(),
        preSaveUrl: String = "",
        pitchBlurb: String = ""
    ): String {
        val releaseDate = displayDateFormatter.format(Date(releaseDateMillis))
        val isoDate = dateFormatter.format(Date(releaseDateMillis))

        val splitsText = if (splits.isEmpty()) {
            "• 100% Owned by Primary Artist / Unassigned"
        } else {
            splits.joinToString("\n") {
                "• ${it.contributorName} (${it.role}): ${it.percentage}% | PRO: ${it.proAffiliation.ifBlank { "N/A" }} (IPI #${it.ipiNumber.ifBlank { "N/A" }})"
            }
        }

        return """
🎵 DISTRIBUTOR METADATA & DELIVERY SPLIT SHEET
═══════════════════════════════════════════════
TRACK TITLE: $title
PRIMARY ARTIST: $artist
${if (featuredArtists.isNotBlank()) "FEATURED ARTISTS: $featuredArtists\n" else ""}TARGET RELEASE DATE: $releaseDate ($isoDate)
DISTRIBUTOR: $distributor

📋 ESSENTIAL CODES
• ISRC Code: ${isrc.ifBlank { "Pending Distributor Generation" }}
• UPC Code: ${upc.ifBlank { "Pending Album/Single UPC" }}
• Explicit Content: ${if (explicit) "⚠️ YES (Explicit)" else "✅ NO (Clean)"}

🎼 MUSICAL METRICS
• Primary Genre: $genre
• Sub-Genre: ${subGenre.ifBlank { "N/A" }}
• BPM / Tempo: $bpm BPM
• Musical Key: $musicalKey

✍️ SONGWRITING & PUBLISHING SPLITS (100% Total)
$splitsText

${if (pitchBlurb.isNotBlank()) "📝 DSP PLAYLIST PITCH BLURB:\n\"$pitchBlurb\"\n\n" else ""}${if (preSaveUrl.isNotBlank()) "🔗 PRE-SAVE LINK:\n$preSaveUrl\n\n" else ""}═══════════════════════════════════════════════
Generated via Song Release Hub for DSP Delivery
""".trimIndent()
    }

    /**
     * Triggers the native Android Share Sheet (`Intent.ACTION_SEND`).
     */
    fun shareViaSystemSheet(
        context: Context,
        content: String,
        subject: String = "Song Metadata & Delivery Sheet",
        mimeType: String = "text/plain"
    ) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            type = mimeType
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Song Metadata via:")
        context.startActivity(shareIntent)
    }

    /**
     * Copies the content to the system clipboard and notifies user.
     */
    fun copyToClipboard(context: Context, label: String, content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, content)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied $label to clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun escapeCsv(value: String): String {
        var clean = value.replace("\r", " ").replace("\n", " ")
        if (clean.contains(",") || clean.contains("\"") || clean.contains(";")) {
            clean = "\"" + clean.replace("\"", "\"\"") + "\""
        }
        return clean
    }
}
