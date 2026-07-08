package com.dites.dinolog.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dites.dinolog.data.local.entity.FeedingLogEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModel
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeedingLogScreen(
    reptileId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    viewModel: ReptileDetailViewModel = viewModel(
        factory = ReptileDetailViewModelFactory(repository, reptileId)
    )
) {
    var feedingDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var foodType by remember { mutableStateOf("") }
    var foodAmount by remember { mutableStateOf("") }
    var accepted by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val foodSuggestions = listOf(
        "Kangkung", "Selada", "Wortel", "Timun", "Labu", 
        "Pepaya", "Pisang", "Semangka", "Kaktus (Opuntia)", 
        "Rumput Gajah", "Dandelion"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Catatan Makan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

            OutlinedTextField(
                value = dateFormatter.format(Date(feedingDate)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Pemberian Makan") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Food Type with Suggestions
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = foodType,
                    onValueChange = { foodType = it },
                    label = { Text("Jenis Makanan") },
                    placeholder = { Text("e.g. Kangkung") },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    foodSuggestions.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { Text(suggestion) },
                            onClick = {
                                foodType = suggestion
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = foodAmount,
                onValueChange = { foodAmount = it },
                label = { Text("Jumlah / Porsi") },
                placeholder = { Text("e.g. Segenggam, 3 lembar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Status")
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = accepted,
                    onClick = { accepted = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Dimakan")
                }
                SegmentedButton(
                    selected = !accepted,
                    onClick = { accepted = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    colors = if (!accepted) SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.errorContainer,
                        activeContentColor = MaterialTheme.colorScheme.error
                    ) else SegmentedButtonDefaults.colors()
                ) {
                    Text("Ditolak")
                }
            }

            if (!accepted) {
                Text(
                    text = "Reptil menolak makan — perhatikan kondisinya",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    viewModel.addFeedingLog(
                        FeedingLogEntity(
                            reptileId = reptileId,
                            feedingDate = feedingDate,
                            foodType = foodType,
                            foodAmount = foodAmount,
                            accepted = accepted,
                            notes = notes
                        )
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = foodType.isNotBlank()
            ) {
                Text("Simpan")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = feedingDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    feedingDate = datePickerState.selectedDateMillis ?: feedingDate
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
