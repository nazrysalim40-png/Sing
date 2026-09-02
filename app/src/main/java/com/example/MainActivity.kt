package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ChecklistScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DropDayLiveScreen
import com.example.ui.screens.MarketingLaunchpadScreen
import com.example.ui.screens.MetadataAndSplitsScreen
import com.example.ui.screens.SavedSongsScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletDark
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SongReleaseViewModel

enum class MainNavTab(val label: String, val icon: ImageVector, val tag: String) {
    LAUNCHPAD("Launchpad", Icons.Default.RocketLaunch, "nav_launchpad"),
    SONGS("Songs", Icons.Default.LibraryMusic, "nav_songs"),
    CHECKLIST("Checklist", Icons.Default.FormatListBulleted, "nav_checklist"),
    METADATA("Splits", Icons.Default.VerifiedUser, "nav_metadata"),
    MARKETING("Marketing", Icons.Default.Campaign, "nav_marketing"),
    DROP_LIVE("Drop Live", Icons.Default.Celebration, "nav_drop_live")
}

class MainActivity : ComponentActivity() {
    private val viewModel: SongReleaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isLightMood by viewModel.isLightMood.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = !isLightMood) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: SongReleaseViewModel) {
    var currentTab by remember { mutableStateOf(MainNavTab.LAUNCHPAD) }
    val colors = AppTheme.colors

    val allReleases by viewModel.allReleases.collectAsStateWithLifecycle()
    val currentRelease by viewModel.currentRelease.collectAsStateWithLifecycle()
    val currentChecklist by viewModel.currentChecklist.collectAsStateWithLifecycle()
    val currentSplits by viewModel.currentSplits.collectAsStateWithLifecycle()
    val currentPromoTasks by viewModel.currentPromoTasks.collectAsStateWithLifecycle()
    val releaseStats by viewModel.releaseStats.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedChecklistCategory.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        containerColor = colors.background,
        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(1.dp, colors.border)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                MainNavTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricViolet,
                            selectedTextColor = if (colors.isLight) ElectricVioletDark else HyperCyan,
                            indicatorColor = ElectricViolet.copy(alpha = if (colors.isLight) 0.15f else 0.2f),
                            unselectedIconColor = colors.textMuted,
                            unselectedTextColor = colors.textMuted
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            Crossfade(
                targetState = currentTab,
                label = "screen_crossfade"
            ) { tab ->
                when (tab) {
                    MainNavTab.LAUNCHPAD -> DashboardScreen(
                        viewModel = viewModel,
                        currentRelease = currentRelease,
                        allReleases = allReleases,
                        stats = releaseStats,
                        checklist = currentChecklist,
                        onNavigateToChecklist = { currentTab = MainNavTab.CHECKLIST },
                        onNavigateToMetadata = { currentTab = MainNavTab.METADATA },
                        onNavigateToMarketing = { currentTab = MainNavTab.MARKETING },
                        onNavigateToDropLive = { currentTab = MainNavTab.DROP_LIVE },
                        onNavigateToSongs = { currentTab = MainNavTab.SONGS }
                    )

                    MainNavTab.SONGS -> SavedSongsScreen(
                        viewModel = viewModel,
                        onSongSelected = {
                            currentTab = MainNavTab.LAUNCHPAD
                        }
                    )

                    MainNavTab.CHECKLIST -> ChecklistScreen(
                        viewModel = viewModel,
                        currentRelease = currentRelease,
                        checklist = currentChecklist,
                        selectedCategory = selectedCategory
                    )

                    MainNavTab.METADATA -> MetadataAndSplitsScreen(
                        viewModel = viewModel,
                        currentRelease = currentRelease,
                        splits = currentSplits
                    )

                    MainNavTab.MARKETING -> MarketingLaunchpadScreen(
                        viewModel = viewModel,
                        currentRelease = currentRelease,
                        promoTasks = currentPromoTasks,
                        splits = currentSplits
                    )

                    MainNavTab.DROP_LIVE -> DropDayLiveScreen(
                        viewModel = viewModel,
                        currentRelease = currentRelease,
                        stats = releaseStats
                    )
                }
            }
        }
    }
}
