package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promo_campaign_tasks")
data class PromoCampaignTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val releaseId: Long,
    val platform: String, // "TikTok", "Instagram Reels", "Spotify Pitch", "YouTube Shorts", "Press Email", "Radio DJ"
    val title: String,
    val contentIdea: String,
    val scheduledDateMillis: Long = System.currentTimeMillis(),
    val isDone: Boolean = false,
    val cost: Double = 0.0
)
