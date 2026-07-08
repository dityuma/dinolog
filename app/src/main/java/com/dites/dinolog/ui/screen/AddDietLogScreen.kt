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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dites.dinolog.data.local.entity.DietLogEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.TortoiseCareViewModel
import com.dites.dinolog.ui.viewmodel.TortoiseCareViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddDietLogScreen(
    reptileId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    viewModel: TortoiseCareViewModel = viewModel(
        factory = TortoiseCareViewModelFactory(repository, reptileId)
    )
) {
    var recordedAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var vegetables by remember { mutableStateOf("") }
    var fruits by remember { mutableStateOf("") }
    var supplements by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val vegPresets = listOf("Kangkung", "Selada", "Wortel", "Timun", "Labu", "Kaktus (Opuntia)", "Rumput Gajah", "Dandelion")
    val supplementPresets = listOf("Kalsium", "Multivitamin", "Kalsium + D3")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Catatan Diet") },
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
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Sayuran berdaun hijau gelap = menu utama. Buah diberikan sesekali saja.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1B5E20)
                )
            }

            val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

            OutlinedTextField(
                value = dateFormatter.format(Date(recordedAt)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Sayuran", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = vegetables,
                onValueChange = { vegetables = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Kangkung, Selada") }
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vegPresets.forEach { preset ->
                    SuggestionChip(
                        onClick = {
                            if (vegetables.isEmpty()) vegetables = preset
                            else if (!vegetables.contains(preset)) vegetables += ", $preset"
                        },
                        label = { Text(preset) }
                    )
                }
            }

            OutlinedTextField(
                value = fruits,
                onValueChange = { fruits = it },
                label = { Text("Buah (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Pepaya, Pisang") }
            )

            Text("Suplemen (opsional)", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = supplements,
                onValueChange = { supplements = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Kalsium") }
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                supplementPresets.forEach { preset ->
                    SuggestionChip(
                        onClick = {
                            if (supplements.isEmpty()) supplements = preset
                            else if (!supplements.contains(preset)) supplements += ", $preset"
                        },
                        label = { Text(preset) }
                    )
                }
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
                    viewModel.addDietLog(
                        DietLogEntity(
                            reptileId = reptileId,
                            recordedAt = recordedAt,
                            vegetables = vegetables,
                            fruits = fruits,
                            supplements = supplements,
                            notes = notes
                        )
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = vegetables.isNotBlank()
            ) {
                Text("Simpan")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = recordedAt)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    recordedAt = datePickerState.selectedDateMillis ?: recordedAt
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
