package com.example.fitnesstrainerapp.screen2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fitnesstrainerapp.R
import com.example.fitnesstrainerapp.ui.theme.FitnessTrainerAppTheme

// --- Data classes to model the UI state ---
data class Challenge(val id: Int, val title: String, val imageUrl: String)
data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)
data class Category(val title: String)

@Composable
fun HomeScreen(
    // Hoist navigation events up to MainActivity
    onNavItemClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onChallengeClick: (Int) -> Unit
) {
    // State should ideally come from a ViewModel
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf(Category("CUSTOM\nPLAN"), Category("LIVE\nSESSION"), Category("DIET\nPLAN"))
    val challenges = listOf(
        Challenge(1, "7 Day Full Body Workout", "https://images.pexels.com/photos/2294361/pexels-photo-2294361.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"),
        Challenge(2, "30 Day Abs Challenge", "https://images.pexels.com/photos/3775131/pexels-photo-3775131.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")
    )
    val bottomNavItems = listOf(
        BottomNavItem("Settings", Icons.Default.Settings, "settings"),
        BottomNavItem("Schedule", Icons.Default.DateRange, "schedule"),
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Alerts", Icons.Default.Notifications, "notifications"),
        BottomNavItem("Profile", Icons.Default.Person, "profile")
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                onItemClick = onNavItemClick,
                currentRoute = "home" // The home screen is always the current route here
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 1. Search Bar
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            }

            // 2. Banner Card
            item {
                Spacer(modifier = Modifier.height(20.dp))
                WorkoutBanner()
            }

            // 3. Category Grid
            item {
                Spacer(modifier = Modifier.height(20.dp))
                CategoryRow(
                    categories = categories,
                    onCategoryClick = onCategoryClick
                )
            }

            // 4. Trending Challenges Section Header
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "TRENDING CHALLENGES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 5. Challenge List Items
            items(challenges, key = { it.id }) { challenge ->
                ChallengeCard(
                    challenge = challenge,
                    onClick = { onChallengeClick(challenge.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// --- Reusable Sub-Components ---

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun WorkoutBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_fitness), // Make sure you have this image
            contentDescription = "Start your workout banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "START\nYOUR\nWORKOUT",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            modifier = Modifier.padding(20.dp).align(Alignment.CenterStart)
        )
    }
}

@Composable
fun CategoryRow(categories: List<Category>, onCategoryClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEach { category ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEFEFEF))
                    .clickable { onCategoryClick(category.title) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: Challenge, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = challenge.imageUrl,
            contentDescription = challenge.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.logo_fitness) // Optional placeholder
        )
        // Scrim for better text readability
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
        Text(
            text = challenge.title,
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun BottomNavigationBar(items: List<BottomNavItem>, onItemClick: (String) -> Unit, currentRoute: String) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = item.route == currentRoute
            NavigationBarItem(
                label = { Text(item.label, fontSize = 10.sp) },
                icon = {
                    if (item.label == "Alerts") { // Example for badged icon
                        BadgedBox(badge = { Badge { Text("8") } }) {
                            Icon(item.icon, contentDescription = item.label)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label)
                    }
                },
                selected = isSelected,
                onClick = { onItemClick(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FitnessTrainerAppTheme {
        HomeScreen(onNavItemClick = {}, onCategoryClick = {}, onChallengeClick = {})
    }
}
