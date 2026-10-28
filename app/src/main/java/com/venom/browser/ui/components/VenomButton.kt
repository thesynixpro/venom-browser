package com.venom.browser.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.browser.ui.theme.VenomGreen
import kotlin.math.roundToInt

data class VenomAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun VenomButton(
    visible: Boolean = true,
    onNewTab: () -> Unit = {},
    onCloseTab: () -> Unit = {},
    onBookmarks: () -> Unit = {},
    onDownloads: () -> Unit = {},
    onFindInPage: () -> Unit = {},
    onShare: () -> Unit = {},
    onSettings: () -> Unit = {},
    onPrivacy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!visible) return

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var expanded by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .scale(scale)
            .shadow(12.dp, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1F21),
                        Color(0xFF0F1314)
                    )
                )
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .clickable {
                pressed = true
                expanded = !expanded
            }
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "VENOM",
                color = VenomGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
        // Subtle green accent line
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp)
                .clip(RoundedCornerShape(50))
                .background(VenomGreen.copy(alpha = 0.6f))
                .padding(horizontal = 16.dp, vertical = 1.dp)
        )
    }

    if (expanded) {
        VenomActionMenu(
            offsetX = offsetX,
            offsetY = offsetY,
            onDismiss = { expanded = false },
            actions = listOf(
                VenomAction(Icons.Default.Add, "New Tab") { onNewTab(); expanded = false },
                VenomAction(Icons.Default.Close, "Close Tab") { onCloseTab(); expanded = false },
                VenomAction(Icons.Default.BookmarkBorder, "Bookmarks") { onBookmarks(); expanded = false },
                VenomAction(Icons.Default.Download, "Downloads") { onDownloads(); expanded = false },
                VenomAction(Icons.Default.FindInPage, "Find in Page") { onFindInPage(); expanded = false },
                VenomAction(Icons.Default.Share, "Share") { onShare(); expanded = false },
                VenomAction(Icons.Default.PrivacyTip, "Privacy") { onPrivacy(); expanded = false },
                VenomAction(Icons.Default.Settings, "Settings") { onSettings(); expanded = false }
            )
        )
    }
}

@Composable
fun VenomActionMenu(
    offsetX: Float,
    offsetY: Float,
    onDismiss: () -> Unit,
    actions: List<VenomAction>
) {
    // Simplified menu - in a full implementation this would be a proper overlay
}
