package com.dites.dinolog.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dites.dinolog.data.local.entity.ReptileEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileListViewModel
import com.dites.dinolog.ui.viewmodel.ReptileListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReptileListScreen(
    repository: DinoLogRepository,
    onNavigateToAddReptile: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: ReptileListViewModel = viewModel(
        factory = ReptileListViewModelFactory(repository)
    )
) {
    val reptiles by viewModel.reptiles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DinoLog") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddReptile) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Reptil")
            }
        }
    ) { padding ->
        if (reptiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada kura-kura. Ketuk + untuk menambahkan kura-kura pertamamu!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reptiles) { reptile ->
                    ReptileCard(
                        reptile = reptile,
                        onClick = { onNavigateToDetail(reptile.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReptileCard(
    reptile: ReptileEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if (reptile.profilePhotoUri.isNotEmpty()) reptile.profilePhotoUri else null,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = reptile.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = reptile.species,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
