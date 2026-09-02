package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Enhanced Press Kit & EPK Pitch Generator Dialog
 * Features:
 * - Biography fields (Short elevator pitch, Full narrative bio, press quotes)
 * - Social Media links (Instagram, Spotify, TikTok, YouTube, Website, Email)
 * - High-Resolution Cover Art Placeholder (3000x3000px, 300 DPI, Asset Download URL)
 * - Multiple Press Kit Templates (Standard EPK, Media Press Release, Curator Pitch, Radio Sheet)
 */
@Composable
fun PressKitGeneratorDialog(
    release: SongRelease,
    splits: List<SongWriterSplit>,
    onDismiss: () -> Unit
) {
    val handleArtist = release.artistName.lowercase().replace(" ", "").replace("[^a-z0-9]".toRegex(), "")

    var shortBio by remember(release) {
        mutableStateOf(
            if (release.pitchBlurb.isNotBlank()) release.pitchBlurb
            else "${release.artistName} is an emerging ${release.genre} artist blending energetic production with poignant lyricism."
        )
    }

    var fullBiography by remember(release) {
        mutableStateOf(
            "${release.artistName} crafts emotive, forward-thinking ${release.genre} records that resonate with listeners worldwide. " +
            "With roots in independent music creation and sonic experimentation, ${release.artistName} has built a dedicated community through immersive live performances and distinct visual aesthetics. " +
            "Their latest single, \"${release.title}\", showcases high-octane arrangements, pristine sound design, and infectious hooks tailored for modern digital streaming."
        )
    }

    var pressQuotes by remember(release) {
        mutableStateOf(
            "\"An undeniable anthem packed with energetic synths and memorable vocal hooks.\" — Indie Beat Journal\n" +
            "\"One of the most promising voices in contemporary ${release.genre}.\" — Soundwave Daily"
        )
    }

    var instagramHandle by remember(release) { mutableStateOf("@$handleArtist.music") }
    var spotifyArtistUrl by remember(release) {
        mutableStateOf(
            if (release.dspSpotifyUrl.isNotBlank()) release.dspSpotifyUrl
            else "https://open.spotify.com/artist/$handleArtist"
        )
    }
    var tiktokHandle by remember(release) { mutableStateOf("@$handleArtist") }
    var youtubeUrl by remember(release) { mutableStateOf("https://youtube.com/@$handleArtist") }
    var websiteUrl by remember(release) { mutableStateOf("https://$handleArtist.com") }
    var contactEmail by remember(release) { mutableStateOf("press@$handleArtist.com") }

    var coverArtAssetUrl by remember(release) {
        mutableStateOf(
            if (release.preSaveUrl.isNotBlank()) "${release.preSaveUrl}/assets/cover-master-3000.png"
            else "https://assets.distrokid.com/art/${handleArtist}/${release.title.lowercase().replace(" ", "-")}-hires.png"
        )
    }

    var selectedTemplateIndex by remember { mutableIntStateOf(0) }

    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val dropDateStr = dateFormat.format(Date(release.releaseDateMillis))

    val generatedEpkContent = remember(
        selectedTemplateIndex, release, splits, shortBio, fullBiography, pressQuotes,
        instagramHandle, spotifyArtistUrl, tiktokHandle, youtubeUrl, websiteUrl, contactEmail, coverArtAssetUrl
    ) {
        when (selectedTemplateIndex) {
            0 -> """
                =======================================================
                ARTIST ELECTRONIC PRESS KIT (EPK)
                =======================================================
                
                ARTIST: ${release.artistName} ${if (release.featuredArtists.isNotBlank()) "feat. ${release.featuredArtists}" else ""}
                CURRENT FOCUS SINGLE: "${release.title}"
                OFFICIAL RELEASE DATE: $dropDateStr
                GENRE / SUB-GENRE: ${release.genre} / ${release.subGenre}
                DISTRIBUTOR: ${release.distributor}
                
                -------------------------------------------------------
                1. ELEVATOR PITCH & SUMMARY
                -------------------------------------------------------
                $shortBio
                
                -------------------------------------------------------
                2. ARTIST BIOGRAPHY
                -------------------------------------------------------
                $fullBiography
                
                -------------------------------------------------------
                3. PRESS HIGHLIGHTS & QUOTES
                -------------------------------------------------------
                $pressQuotes
                
                -------------------------------------------------------
                4. MASTER COVER ART & MEDIA ASSETS
                -------------------------------------------------------
                • High-Res Artwork (3000x3000px, 300 DPI): $coverArtAssetUrl
                • Master Format: Lossless 24-bit / 48kHz WAV (-14 LUFS)
                • Pre-Save Smart Link: ${release.preSaveUrl.ifBlank { "https://ffm.to/${handleArtist}-${release.title.lowercase().replace(" ", "")}" }}
                
                -------------------------------------------------------
                5. OFFICIAL SOCIALS & STREAMING PROFILES
                -------------------------------------------------------
                • Spotify: $spotifyArtistUrl
                • Instagram: https://instagram.com/${instagramHandle.removePrefix("@")} ($instagramHandle)
                • TikTok: https://tiktok.com/@${tiktokHandle.removePrefix("@")} ($tiktokHandle)
                • YouTube: $youtubeUrl
                • Official Website: $websiteUrl
                
                -------------------------------------------------------
                6. TRACK CREDITS & METADATA
                -------------------------------------------------------
                • ISRC: ${release.isrcCode.ifBlank { "Pending Ingestion" }}
                • UPC: ${release.upcCode.ifBlank { "Pending Distributor" }}
                • BPM: ${release.bpm} | Key: ${release.musicalKey}
                • Songwriters & Splits: ${
                    if (splits.isEmpty()) "${release.artistName} (100%)"
                    else splits.joinToString(", ") { "${it.contributorName} (${it.percentage}%, ${it.role})" }
                }
                
                -------------------------------------------------------
                7. PRESS & BOOKING CONTACT
                -------------------------------------------------------
                Direct Inquiries: $contactEmail
            """.trimIndent()

            1 -> """
                📰 PRESS RELEASE & MEDIA OUTREACH
                
                FOR IMMEDIATE RELEASE:
                ${release.artistName} Announces Brand New Single "${release.title}"
                Release Date: $dropDateStr
                
                "${release.title}" is the upcoming ${release.genre} anthem from ${release.artistName}. 
                $shortBio
                
                ARTIST BACKGROUND & STORY:
                $fullBiography
                
                CRITICAL ACCLAIM & PRESS QUOTES:
                $pressQuotes
                
                MASTER MEDIA ASSETS:
                • High-Res Master Artwork (3000x3000px): $coverArtAssetUrl
                • Pre-Save Smart Link: ${release.preSaveUrl}
                
                OFFICIAL SOCIAL LINKS:
                Spotify: $spotifyArtistUrl
                Instagram: $instagramHandle
                TikTok: $tiktokHandle
                YouTube: $youtubeUrl
                Website: $websiteUrl
                
                Press & Interview Requests: $contactEmail
            """.trimIndent()

            2 -> """
                🎵 SPOTIFY EDITORIAL & CURATOR PITCH SHEET
                
                Track Title: ${release.title}
                Artist: ${release.artistName} ${if (release.featuredArtists.isNotBlank()) "(${release.featuredArtists})" else ""}
                Release Date: $dropDateStr
                Genre: ${release.genre} / ${release.subGenre}
                BPM: ${release.bpm} | Key: ${release.musicalKey} | Language: English
                Distributor: ${release.distributor}
                
                STORY & CURATOR PITCH:
                $shortBio
                
                PRODUCTION DETAILS:
                Recorded in 24-bit / 48kHz WAV audio, master loudness normalized to -14 LUFS standard.
                Master Cover Artwork (3000x3000px, 300 DPI): $coverArtAssetUrl
                
                TARGET PLAYLIST MOODS:
                Late Night Drives, Workout Energy, Synthpop Vibes, Indie Discoveries.
                
                DIRECT CONTACT:
                $contactEmail
            """.trimIndent()

            else -> """
                📻 RADIO & DJ SUBMISSION SHEET
                
                Track: ${release.title} (Radio Edit / Main Master)
                Artist: ${release.artistName} ${release.featuredArtists}
                Release Date: $dropDateStr
                Length: 3:24 | BPM: ${release.bpm} | Key: ${release.musicalKey}
                Explicit: ${if (release.explicitLyrics) "YES (Clean edit available)" else "NO (Clean/Radio Friendly)"}
                PRO Affiliations: ${splits.joinToString(" / ") { "${it.contributorName} (${it.proAffiliation})" }}
                
                Master Cover Art (Hi-Res): $coverArtAssetUrl
                Distributor: ${release.distributor}
                Smart Link: ${release.preSaveUrl}
                Inquiries: $contactEmail
            """.trimIndent()
        }
    }

    PressKitEditorModalDialog(
        release = release,
        splits = splits,
        shortBio = shortBio,
        onShortBioChange = { shortBio = it },
        fullBiography = fullBiography,
        onFullBioChange = { fullBiography = it },
        pressQuotes = pressQuotes,
        onPressQuotesChange = { pressQuotes = it },
        instagramHandle = instagramHandle,
        onInstagramChange = { instagramHandle = it },
        spotifyArtistUrl = spotifyArtistUrl,
        onSpotifyChange = { spotifyArtistUrl = it },
        tiktokHandle = tiktokHandle,
        onTiktokChange = { tiktokHandle = it },
        youtubeUrl = youtubeUrl,
        onYoutubeChange = { youtubeUrl = it },
        websiteUrl = websiteUrl,
        onWebsiteChange = { websiteUrl = it },
        contactEmail = contactEmail,
        onEmailChange = { contactEmail = it },
        coverArtAssetUrl = coverArtAssetUrl,
        onCoverArtUrlChange = { coverArtAssetUrl = it },
        selectedTemplateIndex = selectedTemplateIndex,
        onTemplateSelect = { selectedTemplateIndex = it },
        generatedEpkContent = generatedEpkContent,
        onDismiss = onDismiss
    )
}
