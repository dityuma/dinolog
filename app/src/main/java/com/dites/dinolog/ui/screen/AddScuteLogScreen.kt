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
import com.dites.dinolog.data.local.entity.ScuteLogEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModel
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScuteLogScreen(
    reptileId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    viewModel: ReptileDetailViewModel = viewModel(
        factory = ReptileDetailViewModelFactory(repository, reptileId)
    )
) {
    var recordedAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var condition by remember { mutableStateOf("NORMAL") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val conditions = listOf(
        "NORMAL" to "Normal",
        "PIRAMIDING" to "Piramiding",
        "SOFT_SHELL" to "Shell Lunak",
        "RETAK" to "Retak / Luka",
        "JAMUR" to "Jamur / Bercak"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Kondisi Karapas") },
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
                value = dateFormatter.format(Date(recordedAt)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Pencatatan") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = conditions.find { it.first == condition }?.second ?: "Normal",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kondisi Karapas") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    conditions.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                condition = value
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (condition == "PIRAMIDING") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Piramiding bisa disebabkan kelembaban rendah atau protein berlebih",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFE65100),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (condition == "SOFT_SHELL") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Shell lunak memerlukan penanganan segera — hubungi dokter hewan!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan Detail") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    viewModel.addScuteLog(
                        ScuteLogEntity(
                            reptileId = reptileId,
                            recordedAt = recordedAt,
                            condition = condition,
                            notes = notes
                        )
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
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
