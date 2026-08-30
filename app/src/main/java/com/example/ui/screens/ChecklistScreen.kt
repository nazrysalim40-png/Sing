package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ChecklistCategory
import com.example.data.model.ReleaseChecklistItem
import com.example.data.model.SongRelease
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

@Composable
fun ChecklistScreen(
    viewModel: SongReleaseViewModel,
    currentRelease: SongRelease?,
    checklist: List<ReleaseChecklistItem>,
    selectedCategory: ChecklistCategory?,
    modifier: Modifier = Modifier
) {
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val filteredItems = if (selectedCategory == null) {
        checklist
    } else {
        checklist.filter { it.category == selectedCategory }
    }

    val completedCount = filteredItems.count { it.isCompleted }
    val totalCount = filteredItems.size

    if (showAddTaskDialog) {
        AddTaskDialog(
            defaultCategory = selectedCategory ?: ChecklistCategory.AUDIO_MASTER,
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { cat, title, desc ->
                viewModel.addChecklistItem(cat, title, desc)
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
            // Header Title & Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DROP CHECKLIST",
                            color = HyperCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Release Milestones",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = StudioSurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                    ) {
                        Text(
                            text = "$completedCount / $totalCount Done",
                            color = MintGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Horizontal Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isAllSelected = selectedCategory == null
                        CategoryPill(
                            title = "All Steps",
                            icon = Icons.Default.Check,
                            isSelected = isAllSelected,
                            onClick = { viewModel.selectChecklistCategory(null) }
                        )
                    }

                    items(ChecklistCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        val catIcon = when (category) {
                            ChecklistCategory.AUDIO_MASTER -> Icons.Default.GraphicEq
                            ChecklistCategory.ARTWORK_ASSETS -> Icons.Default.Image
                            ChecklistCategory.METADATA_LEGAL -> Icons.Default.VerifiedUser
                            ChecklistCategory.DISTRIBUTION -> Icons.Default.Send
                            ChecklistCategory.MARKETING_PROMO -> Icons.Default.Campaign
                            ChecklistCategory.DROP_DAY -> Icons.Default.Celebration
                        }

                        CategoryPill(
                            title = category.displayName,
                            icon = catIcon,
                            isSelected = isSelected,
                            onClick = { viewModel.selectChecklistCategory(category) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Checklist Items List
            if (filteredItems.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = StudioSurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tasks in this category",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    ChecklistItemRow(
                        item = item,
                        onToggle = { viewModel.toggleChecklistItem(item) },
                        onDelete = { viewModel.deleteChecklistItem(item) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Floating Action Button to Add New Item
        FloatingActionButton(
            onClick = { showAddTaskDialog = true },
            containerColor = ElectricViolet,
            contentColor = Color.White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("fab_add_task")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
        }
    }
}

@Composable
private fun CategoryPill(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) ElectricViolet else StudioSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricViolet else StudioBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else HyperCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ChecklistItemRow(
    item: ReleaseChecklistItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (item.isCompleted) StudioSurfaceCard.copy(alpha = 0.6f) else StudioSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isCompleted) MintGreen.copy(alpha = 0.4f) else StudioBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("checklist_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Custom Checkbox
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (item.isCompleted) MintGreen else Color(0xFF0F0D1C))
                    .border(
                        2.dp,
                        if (item.isCompleted) MintGreen else ElectricViolet.copy(alpha = 0.7f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        color = if (item.isCompleted) TextMuted else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val offsetText = when {
                        item.milestoneDaysOffset == 0 -> "DROP DAY"
                        item.milestoneDaysOffset < 0 -> "${-item.milestoneDaysOffset}D OUT"
                        else -> "+${item.milestoneDaysOffset}D POST"
                    }

                    Text(
                        text = offsetText,
                        color = if (item.milestoneDaysOffset == 0) NeonPink else HyperCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = item.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    defaultCategory: ChecklistCategory,
    onDismiss: () -> Unit,
    onAddTask: (ChecklistCategory, String, String) -> Unit
) {
    var selectedCat by remember { mutableStateOf(defaultCategory) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add Custom Milestone Task",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Instructions / Notes") },
                    maxLines = 2,
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
                            onAddTask(selectedCat, title, description)
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
                    Text("Add Task to Pipeline", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
