package com.venom.browser.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.browser.ui.theme.Background
import com.venom.browser.ui.theme.BorderColor
import com.venom.browser.ui.theme.PrimaryText
import com.venom.browser.ui.theme.SecondaryText
import com.venom.browser.ui.theme.Surface
import com.venom.browser.ui.theme.VenomGreen

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    onNewTab: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    
    val quickLinks = listOf(
        QuickLink("Google", "https://www.google.com"),
        QuickLink("YouTube", "https://www.youtube.com"),
        QuickLink("Wikipedia", "https://www.wikipedia.org"),
        QuickLink("GitHub", "https://github.com")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Brand header
        Text(
            text = "VENOM",
            color = VenomGreen,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp
        )
        Text(
            text = "BROWSER",
            color = PrimaryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 12.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Search bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp)),
            placeholder = { Text("Search or enter address", color = SecondaryText) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = VenomGreen
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VenomGreen,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = PrimaryText,
                unfocusedTextColor = PrimaryText,
                cursorColor = VenomGreen,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(
                onGo = {
                    if (searchText.isNotEmpty()) {
                        onNavigate(searchText)
                    }
                }
            )
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Quick Access",
            color = PrimaryText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(quickLinks.size) { index ->
                val link = quickLinks[index]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onNavigate(link.url) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = link.name.first().toString(),
                            color = VenomGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = link.name,
                        color = SecondaryText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

data class QuickLink(
    val name: String,
    val url: String
)
