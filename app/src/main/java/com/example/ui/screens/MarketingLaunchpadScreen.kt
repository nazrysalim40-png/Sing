package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PromoCampaignTask
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import com.example.ui.components.ArtistPressKitModule
import com.example.ui.components.PressKitGeneratorDialog
import com.example.ui.components.SmartLinkPreviewCard
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
import com.example.ui.viewmodel.SongReleaseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MarketingLaunchpadScreen(
    viewModel: SongReleaseViewModel,
    currentRelease: SongRelease?,
    promoTasks: List<PromoCampaignTask>,
    splits: List<SongWriterSplit>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (currentRelease == null) return

    var showEpkDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val totalCost = promoTasks.sumOf { it.cost }
    val doneCount = promoTasks.count { it.isDone }

    if (showEpkDialog) {
        PressKitGeneratorDialog(
            release = currentRelease,
            splits = splits,
            onDismiss = { showEpkDialog = false }
        )
    }

    if (showAddTaskDialog) {
        AddPromoTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { platform, title, idea, cost ->
                viewModel.addPromoTask(platform, title, idea, System.currentTimeMillis(), cost)
                showAddTaskDialog = false
            }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(StudioBackground)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MARKETING & CAMPAIGNS",
                            color = HyperCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Promo Launchpad",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { showEpkDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HyperCyan,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("button_open_epk")
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EPK Pitch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 1. Interactive Smart Link Pre-Save Hub
            item {
                SmartLinkPreviewCard(release = currentRelease)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. Artist Press Kit (EPK) Module with Biography, Socials & Hi-Res Artwork Placeholder
            item {
                ArtistPressKitModule(
                    release = currentRelease,
                    splits = splits
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Promo Budget & Spend Tracker
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = MintGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Campaign Ad Spend",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Spent $${"%.2f".format(totalCost)} of $${"%.2f".format(currentRelease.targetBudget)} budget",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StudioBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                        ) {
                            Text(
                                text = "$doneCount / ${promoTasks.size} Done",
                                color = HyperCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Campaign Tasks Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scheduled Content & Promos",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "+ Add Task",
                        color = HyperCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showAddTaskDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // 4. Promo Tasks List
            items(promoTasks, key = { it.id }) { task ->
                PromoTaskRow(
                    task = task,
                    onToggle = { viewModel.togglePromoTask(task) },
                    onDelete = { viewModel.deletePromoTask(task) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Floating Action Button to Add Promo
        FloatingActionButton(
            onClick = { showAddTaskDialog = true },
            containerColor = ElectricViolet,
            contentColor = Color.White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Promo")
        }
    }
}

@Composable
private fun PromoTaskRow(
    task: PromoCampaignTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val platformColor = when (task.platform) {
        "TikTok" -> NeonPink
        "Instagram Reels" -> ElectricViolet
        "Spotify Pitch" -> MintGreen
        "Press Email" -> HyperCyan
        "Meta Ads" -> Color(0xFF1877F2)
        else -> AmberWarning
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = StudioSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.isDone) MintGreen.copy(alpha = 0.4f) else StudioBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isDone) MintGreen else Color(0xFF0F0D1C))
                    .border(2.dp, if (task.isDone) MintGreen else ElectricViolet, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (task.isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = platformColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, platformColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = task.platform.uppercase(),
                            color = platformColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (task.cost > 0.0) {
                        Text(
                            text = "$${"%.0f".format(task.cost)}",
                            color = MintGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.title,
                    color = if (task.isDone) TextMuted else TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                if (task.contentIdea.isNotBlank()) {
                    Text(
                        text = task.contentIdea,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun AddPromoTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (platform: String, title: String, idea: String, cost: Double) -> Unit
) {
    var platform by remember { mutableStateOf("TikTok") }
    var title by remember { mutableStateOf("") }
    var idea by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("0") }

    val platforms = listOf("TikTok", "Instagram Reels", "Spotify Pitch", "Press Email", "Meta Ads", "YouTube Shorts")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add Promotional Action",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "PLATFORM / CHANNEL",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    platforms.take(3).forEach { p ->
                        val isSel = platform == p
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) ElectricViolet else StudioBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ElectricViolet else StudioBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { platform = p }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                                Text(
                                    text = p,
                                    color = if (isSel) Color.White else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task / Video Headline *") },
                    placeholder = { Text("e.g. Studio vocal take snippet") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = idea,
                    onValueChange = { idea = it },
                    label = { Text("Concept & Caption Hook") },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("Ad Budget / Cost ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val costVal = costText.toDoubleOrNull() ?: 0.0
                            onAddTask(platform, title, idea, costVal)
                        }
                    },
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricViolet,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Schedule Campaign Action", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
