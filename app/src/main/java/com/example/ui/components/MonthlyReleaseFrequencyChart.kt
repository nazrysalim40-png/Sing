package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Song
import com.example.data.model.SongRelease
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletDark
import com.example.ui.theme.ElectricVioletLight
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.HyperCyanDark
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Data structure representing release metrics for a specific month.
 */
data class MonthReleaseData(
    val monthIndex: Int, // 0 = Jan, 11 = Dec
    val monthShortName: String,
    val monthFullName: String,
    val year: Int,
    val songs: List<SongSummary>,
    val count: Int
)

data class SongSummary(
    val id: Long,
    val title: String,
    val artist: String,
    val genre: String,
    val releaseDateMillis: Long,
    val isrcCode: String = ""
)

/**
 * Native Jetpack Compose visualization of total songs released by month.
 * Helps artists track and analyze their release frequency, consistency, and cadence
 * over the calendar year with interactive monthly drill-downs and smart cadence indicators.
 */
@Composable
fun MonthlyReleaseFrequencyChart(
    songs: List<Song>,
    modifier: Modifier = Modifier,
    initialYear: Int? = null,
    onAddReleaseForMonth: ((year: Int, monthIndex: Int) -> Unit)? = null
) {
    val currentCalendar = remember { Calendar.getInstance() }
    val currentActualYear = currentCalendar.get(Calendar.YEAR)
    val currentActualMonth = currentCalendar.get(Calendar.MONTH)

    var selectedYear by remember { mutableIntStateOf(initialYear ?: currentActualYear) }
    var selectedMonthIndex by remember { mutableStateOf<Int?>(currentActualMonth) }

    val monthNamesShort = remember {
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    }
    val monthNamesFull = remember {
        listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
    }

    // Aggregate monthly data for the selected year
    val monthlyData = remember(songs, selectedYear) {
        val cal = Calendar.getInstance()
        val monthBuckets = Array(12) { mutableListOf<SongSummary>() }

        songs.forEach { song ->
            cal.timeInMillis = song.releaseDateMillis
            val songYear = cal.get(Calendar.YEAR)
            val songMonth = cal.get(Calendar.MONTH)
            if (songYear == selectedYear && songMonth in 0..11) {
                monthBuckets[songMonth].add(
                    SongSummary(
                        id = song.id,
                        title = song.title,
                        artist = song.artistName,
                        genre = song.genre,
                        releaseDateMillis = song.releaseDateMillis,
                        isrcCode = song.isrcCode
                    )
                )
            }
        }

        monthNamesShort.mapIndexed { index, shortName ->
            val bucket = monthBuckets[index]
            MonthReleaseData(
                monthIndex = index,
                monthShortName = shortName,
                monthFullName = monthNamesFull[index],
                year = selectedYear,
                songs = bucket.sortedBy { it.releaseDateMillis },
                count = bucket.size
            )
        }
    }

    val totalReleasesInYear = remember(monthlyData) { monthlyData.sumOf { it.count } }
    val maxReleasesInMonth = remember(monthlyData) { (monthlyData.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1) }
    val chartYAxisMax = remember(maxReleasesInMonth) {
        when {
            maxReleasesInMonth <= 3 -> 4
            maxReleasesInMonth <= 5 -> 6
            maxReleasesInMonth <= 8 -> 10
            else -> maxReleasesInMonth + 2
        }
    }

    val peakMonth = remember(monthlyData) {
        monthlyData.filter { it.count > 0 }.maxByOrNull { it.count }
    }

    val activeMonthsCount = remember(monthlyData) { monthlyData.count { it.count > 0 } }
    val avgCadence = remember(totalReleasesInYear) {
        if (totalReleasesInYear > 0) {
            String.format(Locale.US, "%.1f", totalReleasesInYear / 12.0)
        } else {
            "0.0"
        }
    }

    val cadenceHealth = remember(totalReleasesInYear, activeMonthsCount) {
        when {
            totalReleasesInYear >= 10 -> Triple("Optimal Cadence", "Consistent monthly drip strategy (Algorithmic favorite)", MintGreen)
            totalReleasesInYear in 5..9 -> Triple("Steady Momentum", "Bi-monthly release cycle (Strong audience retention)", HyperCyan)
            totalReleasesInYear in 1..4 -> Triple("Active Releases", "Targeting key single milestones", AmberWarning)
            else -> Triple("Planning Stage", "No drops recorded for this year yet", TextMuted)
        }
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = BorderStroke(1.dp, StudioBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_release_frequency_chart")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top Bar: Title & Year Switcher
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
                            .background(ElectricViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = HyperCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "RELEASE CADENCE ANALYTICS",
                            color = HyperCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.4.sp
                        )
                        Text(
                            text = "Monthly Release Frequency",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Year Switcher Controller
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StudioSurfaceHover,
                    border = BorderStroke(1.dp, StudioBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { selectedYear -= 1 },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("chart_year_previous")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Year",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = selectedYear.toString(),
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .testTag("chart_selected_year")
                        )

                        IconButton(
                            onClick = { selectedYear += 1 },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("chart_year_next")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Year",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Year Summary KPI Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total Drops
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StudioSurfaceHover,
                    border = BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Total Releases",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$totalReleasesInYear",
                                color = HyperCyan,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "songs",
                                color = TextMuted,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }

                // Avg Pace
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StudioSurfaceHover,
                    border = BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Average Pace",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$avgCadence",
                                color = ElectricVioletLight,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "/ mo",
                                color = TextMuted,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }

                // Peak Month
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StudioSurfaceHover,
                    border = BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Peak Month",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (peakMonth != null) "${peakMonth.monthShortName} (${peakMonth.count})" else "—",
                            color = MintGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cadence Status Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = cadenceHealth.third.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, cadenceHealth.third.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = cadenceHealth.third,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = cadenceHealth.first,
                            color = cadenceHealth.third,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = cadenceHealth.second,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ----------------------------------------------------
            // Visual Bar Chart Canvas with Y-Axis and Interactive Bars
            // ----------------------------------------------------
            Text(
                text = "Tap a column to inspect songs or schedule releases:",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(StudioSurface, RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                // Render the 12 month columns with Canvas & Interactive Overlays
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    monthlyData.forEach { monthItem ->
                        val isSelected = selectedMonthIndex == monthItem.monthIndex
                        val isCurrentActualMonth = (selectedYear == currentActualYear && monthItem.monthIndex == currentActualMonth)
                        val animatedHeightFraction by animateFloatAsState(
                            targetValue = if (chartYAxisMax > 0) (monthItem.count.toFloat() / chartYAxisMax).coerceIn(0f, 1f) else 0f,
                            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                            label = "barHeightAnim_${monthItem.monthShortName}"
                        )

                        MonthBarColumn(
                            monthData = monthItem,
                            heightFraction = animatedHeightFraction,
                            isSelected = isSelected,
                            isCurrentActualMonth = isCurrentActualMonth,
                            onMonthClicked = {
                                selectedMonthIndex = if (selectedMonthIndex == monthItem.monthIndex) null else monthItem.monthIndex
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("month_bar_${monthItem.monthShortName}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ----------------------------------------------------
            // Selected Month Detailed Breakdown Card
            // ----------------------------------------------------
            AnimatedVisibility(visible = selectedMonthIndex != null) {
                val activeMonth = monthlyData.getOrNull(selectedMonthIndex ?: -1)
                if (activeMonth != null) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceHover),
                        border = BorderStroke(1.dp, if (activeMonth.count > 0) HyperCyan.copy(alpha = 0.5f) else StudioBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("month_detail_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = if (activeMonth.count > 0) HyperCyan else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${activeMonth.monthFullName} $selectedYear",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (activeMonth.count > 0) HyperCyan.copy(alpha = 0.2f) else StudioSurfaceCard,
                                    border = BorderStroke(1.dp, if (activeMonth.count > 0) HyperCyan else StudioBorder)
                                ) {
                                    Text(
                                        text = "${activeMonth.count} ${if (activeMonth.count == 1) "Release" else "Releases"}",
                                        color = if (activeMonth.count > 0) HyperCyan else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (activeMonth.songs.isNotEmpty()) {
                                activeMonth.songs.forEachIndexed { idx, song ->
                                    if (idx > 0) {
                                        Divider(
                                            color = StudioBorder,
                                            thickness = 0.5.dp,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(ElectricViolet.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = null,
                                                    tint = ElectricVioletLight,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column {
                                                Text(
                                                    text = song.title,
                                                    color = TextPrimary,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${song.artist} • ${song.genre}",
                                                    color = TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = dateFormatter.format(Date(song.releaseDateMillis)),
                                                color = HyperCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            if (song.isrcCode.isNotBlank()) {
                                                Text(
                                                    text = song.isrcCode,
                                                    color = TextMuted,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "No songs scheduled for this month.",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )

                                    if (onAddReleaseForMonth != null) {
                                        Button(
                                            onClick = { onAddReleaseForMonth(selectedYear, activeMonth.monthIndex) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ElectricViolet,
                                                contentColor = Color.White
                                            ),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Plan Drop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual month column in the release frequency chart.
 */
@Composable
private fun MonthBarColumn(
    monthData: MonthReleaseData,
    heightFraction: Float,
    isSelected: Boolean,
    isCurrentActualMonth: Boolean,
    onMonthClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onMonthClicked() }
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Count number indicator badge on top of bar
        if (monthData.count > 0) {
            Text(
                text = "${monthData.count}",
                color = if (isSelected) HyperCyan else TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Custom Painted Bar with gradient, rounded corners, and active pulse
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(95.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (isCurrentActualMonth) ElectricViolet.copy(alpha = 0.15f) else StudioSurfaceHover.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                    )
            )

            // Fill Pillar
            if (heightFraction > 0.02f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((95 * heightFraction).dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (isSelected) {
                                    listOf(HyperCyan, ElectricVioletLight)
                                } else if (monthData.count >= 2) {
                                    listOf(MintGreen, ElectricViolet)
                                } else {
                                    listOf(ElectricVioletLight, ElectricVioletDark)
                                }
                            ),
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                        )
                )
            } else if (isCurrentActualMonth) {
                // Dot indicator for current month with 0 releases
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .padding(bottom = 2.dp)
                        .clip(CircleShape)
                        .background(HyperCyan.copy(alpha = 0.6f))
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Month Short Label
        Text(
            text = monthData.monthShortName,
            color = when {
                isSelected -> HyperCyan
                isCurrentActualMonth -> MintGreen
                monthData.count > 0 -> TextPrimary
                else -> TextSecondary
            },
            fontSize = 9.sp,
            fontWeight = if (isSelected || isCurrentActualMonth || monthData.count > 0) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}
