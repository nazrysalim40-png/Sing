package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Dialog wrapper for the NewReleaseForm allowing modal data entry to add
 * a new music release to the Room database.
 */
@Composable
fun NewReleaseDialog(
    onDismiss: () -> Unit,
    onCreateRelease: (
        title: String,
        artist: String,
        featured: String,
        genre: String,
        subGenre: String,
        releaseDateMillis: Long,
        bpm: Int,
        key: String,
        distributor: String,
        coverArtPreset: String,
        pitchBlurb: String
    ) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("new_release_dialog")
        ) {
            NewReleaseForm(
                onSaveRelease = { title, artist, featured, genre, subGenre, dateMillis, bpm, key, dist, art, pitch ->
                    onCreateRelease(title, artist, featured, genre, subGenre, dateMillis, bpm, key, dist, art, pitch)
                    onDismiss()
                },
                onCancel = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
