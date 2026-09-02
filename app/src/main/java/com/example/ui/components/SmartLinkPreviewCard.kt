package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ReleaseStatus
import com.example.data.model.SongRelease
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SmartLinkPreviewCard(
    release: SongRelease,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDropped = release.status == ReleaseStatus.DROPPED
    val actionText = if (isDropped) "PLAY" else "PRE-SAVE"

    val coverArtRes = when (release.coverArtPreset) {
        "acoustic" -> R.drawable.img_release_cover_acoustic
        "studio" -> R.drawable.img_studio_hero
        "light" -> R.drawable.img_release_cover_light
        else -> R.drawable.img_release_cover_neon
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "Smart Link",
                        tint = HyperCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isDropped) "Live Smart Link Hub" else "Pre-Save Fan Landing Page",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Smart Link", release.preSaveUrl)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Smart link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Link",
                        tint = HyperCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Landing Page Mockup Box
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0D0B1A),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Album Cover Center
                    Image(
                        painter = painterResource(id = coverArtRes),
                        contentDescription = release.title,
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, StudioBorder, RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = release.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = release.artistName,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // DSP List
                    DspRowItem(
                        platformName = "Spotify",
                        platformColor = Color(0xFF1DB954),
                        action = actionText,
                        onClick = {
                            Toast.makeText(context, "Redirecting to Spotify: $actionText", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DspRowItem(
                        platformName = "Apple Music",
                        platformColor = Color(0xFFFA243C),
                        action = actionText,
                        onClick = {
                            Toast.makeText(context, "Redirecting to Apple Music: $actionText", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DspRowItem(
                        platformName = "YouTube Music",
                        platformColor = Color(0xFFFF0000),
                        action = actionText,
                        onClick = {
                            Toast.makeText(context, "Redirecting to YouTube Music: $actionText", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DspRowItem(
                        platformName = "Amazon Music",
                        platformColor = Color(0xFF00A8E1),
                        action = actionText,
                        onClick = {
                            Toast.makeText(context, "Redirecting to Amazon Music: $actionText", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DspRowItem(
    platformName: String,
    platformColor: Color,
    action: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = StudioSurfaceHover,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(platformColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = platformName,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (action == "PLAY") MintGreen else ElectricViolet,
                    contentColor = if (action == "PLAY") Color.Black else Color.White
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text(
                    text = action,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
