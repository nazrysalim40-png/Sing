package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletDark
import com.example.ui.theme.ElectricVioletLight
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.HyperCyanDark
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
 * Data holder for Press Kit Template Fields
 */
data class ArtistPressKitState(
    val shortBio: String = "",
    val fullBiography: String = "",
    val pressQuotes: String = "",
    val instagramHandle: String = "",
    val spotifyArtistUrl: String = "",
    val tiktokHandle: String = "",
    val youtubeUrl: String = "",
    val websiteUrl: String = "",
    val contactEmail: String = "",
    val coverArtUrl: String = "",
    val selectedTemplateIndex: Int = 0
)

/**
 * Interactive Artist Press Kit Module & Generator
 * Provides:
 * - Structured Biography editor (Elevator pitch, full biography, press quotes)
 * - Social Media & Streaming Links (Instagram, Spotify, TikTok, YouTube, Website, Contact Email)
 * - High-Resolution 3000x3000px Cover Art Placeholder Card with download/copy asset affordance
 * - Ready-to-use EPK Templates (Standard EPK, Media Press Release, Curator Pitch, Booking Sheet)
 * - Real-time Formatted Press Kit generator with instant 1-tap clipboard copy & system share
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArtistPressKitModule(
    release: SongRelease,
    splits: List<SongWriterSplit> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    var showFullEditorDialog by remember { mutableStateOf(false) }

    // Biography State
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

    // Social Media Links State
    val handleArtist = release.artistName.lowercase().replace(" ", "").replace("[^a-z0-9]".toRegex(), "")
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

    // High-Res Cover Art Placeholder Details
    var coverArtDimensions by remember { mutableStateOf("3000 × 3000 PX") }
    var coverArtDpi by remember { mutableStateOf("300 DPI (Lossless RGB)") }
    var coverArtAssetUrl by remember(release) {
        mutableStateOf(
            if (release.preSaveUrl.isNotBlank()) "${release.preSaveUrl}/assets/cover-master-3000.png"
            else "https://assets.distrokid.com/art/${handleArtist}/${release.title.lowercase().replace(" ", "-")}-hires.png"
        )
    }

    // Active Template Index
    var selectedTemplateIndex by remember { mutableIntStateOf(0) }

    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val dropDateStr = dateFormat.format(Date(release.releaseDateMillis))

    // Formatted EPK text output based on template
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
                FOR IMMEDIATE RELEASE:
                ${release.artistName} Announces Brand New Single "${release.title}"
                Release Date: $dropDateStr
                
                ${release.artistName} returns with "${release.title}", an evocative new ${release.genre} release set to hit all streaming services on $dropDateStr.
                
                ABOUT THE TRACK:
                $shortBio
                
                ARTIST BACKGROUND:
                $fullBiography
                
                CRITICAL ACCLAIM:
                $pressQuotes
                
                HIGH-RESOLUTION ASSETS FOR EDITORIAL USE:
                Master Cover Artwork (3000x3000px): $coverArtAssetUrl
                Smart Link & Streaming: ${release.preSaveUrl}
                
                CONNECT WITH ${release.artistName.uppercase()}:
                Spotify: $spotifyArtistUrl
                Instagram: $instagramHandle
                TikTok: $tiktokHandle
                YouTube: $youtubeUrl
                Website: $websiteUrl
                
                Media & Interview Requests: $contactEmail
            """.trimIndent()

            2 -> """
                🎵 CURATOR & PLAYLIST PITCH SHEET
                
                Track Title: ${release.title}
                Main Artist: ${release.artistName} ${if (release.featuredArtists.isNotBlank()) "(${release.featuredArtists})" else ""}
                Release Date: $dropDateStr
                Genre / Style: ${release.genre} / ${release.subGenre}
                BPM / Key: ${release.bpm} BPM / ${release.musicalKey}
                
                CURATOR BLURB:
                $shortBio
                
                SOUND COMPARISONS & PLAYLIST MOODS:
                Energetic, Late Night Drive, Indie Pop Hits, Fresh Finds, Viral Energy.
                
                STREAMING & PROFILES:
                Spotify Artist: $spotifyArtistUrl
                Master Cover Art (Hi-Res): $coverArtAssetUrl
                
                Direct Contact: $contactEmail
            """.trimIndent()

            else -> """
                📋 BOOKING & FESTIVAL ONE-SHEET
                
                ACT: ${release.artistName}
                GENRE: ${release.genre}
                LOCATION: Independent Artist
                
                BIO SUMMARY:
                $shortBio
                
                FULL BIO:
                $fullBiography
                
                LIVE REPERTOIRE / LATEST SINGLE:
                "${release.title}" (Dropped $dropDateStr)
                
                PRESS QUOTES:
                $pressQuotes
                
                ONLINE PRESENCE:
                • Spotify: $spotifyArtistUrl
                • Instagram: $instagramHandle
                • TikTok: $tiktokHandle
                • YouTube: $youtubeUrl
                • Web: $websiteUrl
                • Promo Photos & Cover Art: $coverArtAssetUrl
                
                Booking Inquiries: $contactEmail
            """.trimIndent()
        }
    }

    if (showFullEditorDialog) {
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
            onDismiss = { showFullEditorDialog = false }
        )
    }

    // Main Card Module
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = BorderStroke(1.dp, StudioBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_artist_press_kit_module")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Module Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(ElectricViolet.copy(alpha = 0.35f), NeonPink.copy(alpha = 0.35f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = ElectricVioletLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "MEDIA & PROMO ASSETS",
                            color = ElectricVioletLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.3.sp
                        )
                        Text(
                            text = "Artist Press Kit (EPK)",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showFullEditorDialog = true },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("button_open_epk_full_editor")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Fields",
                            tint = HyperCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // High-Resolution Cover Art Placeholder Preview Row
            HighResolutionCoverArtPlaceholder(
                release = release,
                coverArtUrl = coverArtAssetUrl,
                dimensions = coverArtDimensions,
                dpi = coverArtDpi,
                onCopyUrl = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Cover Art URL", coverArtAssetUrl)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Cover art asset link copied!", Toast.LENGTH_SHORT).show()
                },
                onEditArt = { showFullEditorDialog = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Info Badges: Biography & Social Media Links Summary
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StudioBackground.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = HyperCyan, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Biography & Pitch Blurb", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Edit",
                            color = HyperCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showFullEditorDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = shortBio,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        maxLines = if (isExpanded) 6 else 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = StudioBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Social Media Chips Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = NeonPink, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Social & Web Links", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SocialMiniChip(icon = Icons.Default.MusicNote, label = "Spotify", value = "Connected", color = MintGreen)
                        SocialMiniChip(icon = Icons.Default.Public, label = "Instagram", value = instagramHandle, color = NeonPink)
                        SocialMiniChip(icon = Icons.Default.Public, label = "TikTok", value = tiktokHandle, color = HyperCyan)
                        SocialMiniChip(icon = Icons.Default.Email, label = "Press Email", value = contactEmail, color = AmberWarning)
                    }
                }
            }

            // Expanded Section: Template Tabs & Live Generated EPK Preview
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Text(
                        text = "CHOOSE PRESS KIT TEMPLATE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Template Tabs Selector
                    val templates = listOf("Standard EPK", "Press Release", "Curator Pitch", "Booking Sheet")
                    TabRow(
                        selectedTabIndex = selectedTemplateIndex,
                        containerColor = StudioSurfaceHover,
                        contentColor = HyperCyan,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTemplateIndex]),
                                color = HyperCyan,
                                height = 3.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        templates.forEachIndexed { idx, name ->
                            Tab(
                                selected = selectedTemplateIndex == idx,
                                onClick = { selectedTemplateIndex = idx },
                                text = {
                                    Text(
                                        text = name,
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedTemplateIndex == idx) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Formatted Output Preview Container
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0C0A16),
                        border = BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LIVE EPK OUTPUT PREVIEW",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "${generatedEpkContent.length} chars",
                                    color = HyperCyan,
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = generatedEpkContent,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp,
                                maxLines = 14,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row (Copy Formatted EPK, Share, Open Editor)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Artist Press Kit", generatedEpkContent)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Artist Press Kit copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HyperCyan,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("button_copy_epk_template")
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Press Kit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, generatedEpkContent)
                            putExtra(Intent.EXTRA_SUBJECT, "EPK & Press Kit: ${release.artistName} - \"${release.title}\"")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share Artist Press Kit")
                        context.startActivity(shareIntent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, StudioBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = StudioSurfaceHover,
                        contentColor = TextPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("button_share_epk_template")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showFullEditorDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ElectricViolet.copy(alpha = 0.12f),
                        contentColor = ElectricVioletLight
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("button_customize_epk")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Customize", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Visual High-Resolution 3000x3000px Cover Art Placeholder Card
 */
@Composable
fun HighResolutionCoverArtPlaceholder(
    release: SongRelease,
    coverArtUrl: String,
    dimensions: String = "3000 × 3000 PX",
    dpi: String = "300 DPI (Lossless RGB)",
    onCopyUrl: () -> Unit,
    onEditArt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = StudioBackground.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, StudioBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_hires_cover_art_placeholder")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Stylized Cover Art Mock Preview Box
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (release.coverArtPreset) {
                            "neon" -> Brush.radialGradient(listOf(NeonPink, ElectricVioletDark, Color(0xFF0F0B1E)))
                            "acoustic" -> Brush.radialGradient(listOf(AmberWarning, Color(0xFF6D4C41), Color(0xFF1E1714)))
                            "light" -> Brush.radialGradient(listOf(Color(0xFFFFB300), Color(0xFFFF80AB), Color(0xFF00E5FF)))
                            else -> Brush.radialGradient(listOf(HyperCyan, Color(0xFF00695C), Color(0xFF071B19)))
                        }
                    )
                    .border(1.5.dp, HyperCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HighQuality,
                        contentDescription = "Hi-Res Master",
                        tint = HyperCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "3000×3000",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "300 DPI",
                        color = MintGreen,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right: Cover Art Metadata Specs & Action Links
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "High-Res Master Cover Art",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MintGreen.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, MintGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "READY FOR DSP",
                            color = MintGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Resolution: $dimensions • $dpi",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Text(
                    text = "Asset Link: ${coverArtUrl.take(30)}...",
                    color = HyperCyan,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StudioSurfaceHover,
                        border = BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.clickable { onCopyUrl() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = HyperCyan, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Asset Link", color = HyperCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StudioSurfaceHover,
                        border = BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.clickable { onEditArt() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Details", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Detailed Full Editor & Customizer Modal Dialog for Artist Press Kit
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PressKitEditorModalDialog(
    release: SongRelease,
    splits: List<SongWriterSplit>,
    shortBio: String,
    onShortBioChange: (String) -> Unit,
    fullBiography: String,
    onFullBioChange: (String) -> Unit,
    pressQuotes: String,
    onPressQuotesChange: (String) -> Unit,
    instagramHandle: String,
    onInstagramChange: (String) -> Unit,
    spotifyArtistUrl: String,
    onSpotifyChange: (String) -> Unit,
    tiktokHandle: String,
    onTiktokChange: (String) -> Unit,
    youtubeUrl: String,
    onYoutubeChange: (String) -> Unit,
    websiteUrl: String,
    onWebsiteChange: (String) -> Unit,
    contactEmail: String,
    onEmailChange: (String) -> Unit,
    coverArtAssetUrl: String,
    onCoverArtUrlChange: (String) -> Unit,
    selectedTemplateIndex: Int,
    onTemplateSelect: (Int) -> Unit,
    generatedEpkContent: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableIntStateOf(0) } // 0: Bio & Story, 1: Socials & Links, 2: Cover Art, 3: Generated EPK

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
            border = BorderStroke(1.dp, StudioBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("dialog_press_kit_editor")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(HyperCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = HyperCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "PRESS KIT GENERATOR",
                                color = HyperCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Artist EPK & Assets",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = StudioSurfaceHover,
                    contentColor = HyperCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                            color = HyperCyan,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Biography", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Socials", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Cover Art", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        text = { Text("Template", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (activeTab) {
                    0 -> {
                        // TAB 0: BIOGRAPHY & QUOTES
                        Text(
                            text = "Elevator Pitch / Hook Blurb",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = shortBio,
                            onValueChange = onShortBioChange,
                            placeholder = { Text("1-2 sentence hook describing the song and artist vibe...", color = TextMuted, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_short_bio"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2,
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Full Artist Biography",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = fullBiography,
                            onValueChange = onFullBioChange,
                            placeholder = { Text("Complete narrative biography for media and curators...", color = TextMuted, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_full_bio"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 4,
                            maxLines = 7
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Press Quotes & Notable Highlights",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = pressQuotes,
                            onValueChange = onPressQuotesChange,
                            placeholder = { Text("\"Standout quote from music blogs or publications...\"", color = TextMuted, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_press_quotes"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2,
                            maxLines = 4
                        )
                    }

                    1 -> {
                        // TAB 1: SOCIAL MEDIA & STREAMING LINKS
                        Text("Social Media & Streaming Profiles", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Instagram
                        OutlinedTextField(
                            value = instagramHandle,
                            onValueChange = onInstagramChange,
                            label = { Text("Instagram Handle") },
                            placeholder = { Text("@artistname") },
                            leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, tint = NeonPink) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_instagram"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPink,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Spotify Artist URL
                        OutlinedTextField(
                            value = spotifyArtistUrl,
                            onValueChange = onSpotifyChange,
                            label = { Text("Spotify Artist / Profile URL") },
                            placeholder = { Text("https://open.spotify.com/artist/...") },
                            leadingIcon = { Icon(Icons.Default.MusicNote, contentDescription = null, tint = MintGreen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_spotify"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MintGreen,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // TikTok
                        OutlinedTextField(
                            value = tiktokHandle,
                            onValueChange = onTiktokChange,
                            label = { Text("TikTok Handle") },
                            placeholder = { Text("@artistname") },
                            leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, tint = HyperCyan) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_tiktok"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // YouTube
                        OutlinedTextField(
                            value = youtubeUrl,
                            onValueChange = onYoutubeChange,
                            label = { Text("YouTube Channel URL") },
                            placeholder = { Text("https://youtube.com/@...") },
                            leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFFFF5252)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_youtube"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF5252),
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Website URL
                        OutlinedTextField(
                            value = websiteUrl,
                            onValueChange = onWebsiteChange,
                            label = { Text("Official Website URL") },
                            placeholder = { Text("https://artistname.com") },
                            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = ElectricViolet) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_website"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Press Contact Email
                        OutlinedTextField(
                            value = contactEmail,
                            onValueChange = onEmailChange,
                            label = { Text("Press & Booking Contact Email") },
                            placeholder = { Text("press@artistname.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AmberWarning) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_email"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberWarning,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    2 -> {
                        // TAB 2: HIGH-RESOLUTION COVER ART PLACEHOLDER & SPECS
                        Text("High-Resolution Cover Art Asset", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual Master Artwork Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(ElectricViolet.copy(alpha = 0.4f), Color(0xFF0B0914))
                                    )
                                )
                                .border(1.5.dp, HyperCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = HyperCyan,
                                    modifier = Modifier.size(42.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${release.title} (Official Master Cover)",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "3000 × 3000 Pixels • 300 DPI • 1:1 Aspect Ratio",
                                    color = MintGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // DSP Artwork Compliance Checklist
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StudioBackground,
                            border = BorderStroke(1.dp, StudioBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("DSP Guidelines & Quality Checklist", color = HyperCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                QualityCheckItem("3000 x 3000 px perfect square resolution", true)
                                QualityCheckItem("RGB Color Mode (lossless TIFF or PNG/JPEG)", true)
                                QualityCheckItem("No website URLs, barcodes, or retailer logos", true)
                                QualityCheckItem("Matches release title and artist name exactly", true)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = coverArtAssetUrl,
                            onValueChange = onCoverArtUrlChange,
                            label = { Text("High-Res Asset Download / CDN URL") },
                            placeholder = { Text("https://cloud.storage/assets/cover-3000px.png") },
                            leadingIcon = { Icon(Icons.Default.CloudDownload, contentDescription = null, tint = HyperCyan) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_epk_cover_url"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedContainerColor = StudioBackground,
                                unfocusedContainerColor = StudioBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    3 -> {
                        // TAB 3: TEMPLATES & GENERATED OUTPUT
                        Text("Select EPK Output Format", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        val templateOptions = listOf("Full Artist EPK", "Media Press Release", "Curator Pitch", "Booking One-Sheet")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            templateOptions.forEachIndexed { idx, name ->
                                val isSel = selectedTemplateIndex == idx
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSel) HyperCyan else StudioBackground,
                                    border = BorderStroke(1.dp, if (isSel) HyperCyan else StudioBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onTemplateSelect(idx) }
                                ) {
                                    Text(
                                        text = name,
                                        color = if (isSel) Color.Black else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF090712),
                            border = BorderStroke(1.dp, StudioBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = generatedEpkContent,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Dialog Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Artist Press Kit", generatedEpkContent)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Press kit copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HyperCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("button_dialog_copy_epk")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Formatted EPK", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, StudioBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = StudioSurfaceHover,
                            contentColor = TextPrimary
                        )
                    ) {
                        Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialMiniChip(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$label: $value",
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun QualityCheckItem(text: String, isOk: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (isOk) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (isOk) MintGreen else Color(0xFFFF5252),
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 10.sp
        )
    }
}
