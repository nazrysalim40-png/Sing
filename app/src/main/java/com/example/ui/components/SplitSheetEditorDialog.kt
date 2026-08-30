package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SongWriterSplit
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurfaceCard

/**
 * Dialog wrapper for the SongSplitSheetForm allowing modal data entry of collaborators
 * and their copyright percentages.
 */
@Composable
fun SplitSheetEditorDialog(
    splits: List<SongWriterSplit>,
    onSaveSplits: (List<SongWriterSplit>) -> Unit,
    onExportClicked: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SongSplitSheetForm(
                initialSplits = splits,
                onSaveSplits = { updatedList ->
                    onSaveSplits(updatedList)
                    onDismiss()
                },
                onExportClicked = onExportClicked,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
