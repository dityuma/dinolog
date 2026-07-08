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
import com.dites.dinolog.data.local.entity.HealthRecordEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModel
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordScreen(
    reptileId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    viewModel: ReptileDetailViewModel = viewModel(
        factory = ReptileDetailViewModelFactory(repository, reptileId)
    )
) {
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var type by remember { mutableStateOf("VET_VISIT") }
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var nextReminderAt by remember { mutableStateOf<Long?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showReminderDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Catatan Kesehatan") },
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
                value = dateFormatter.format(Date(date)),
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

            // Type Dropdown
            var expanded by remember { mutableStateOf(false) }
            val typeOptions = listOf(
                "VET_VISIT" to "Kunjungan Dokter Hewan",
                "MEDICATION" to "Pemberian Obat",
                "PARASITE_TREATMENT" to "Antiparasit",
                "OTHER" to "Lainnya"
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = typeOptions.find { it.first == type }?.second ?: "Kunjungan Dokter Hewan",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Jenis Catatan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    typeOptions.forEach { selection ->
                        DropdownMenuItem(
                            text = { Text(selection.second) },
                            onClick = {
                                type = selection.first
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul") },
                placeholder = { Text("e.g. Vaksin rutin") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank(),
                supportingText = { if (title.isBlank()) Text("Judul wajib diisi") }
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan Detail") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = nextReminderAt?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Pengingat Berikutnya (opsional)") },
                trailingIcon = {
                    IconButton(onClick = { showReminderDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addHealthRecord(
                            HealthRecordEntity(
                                reptileId = reptileId,
                                date = date,
                                type = type,
                                title = title,
                                notes = notes,
                                nextReminderAt = nextReminderAt
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Simpan")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    date = datePickerState.selectedDateMillis ?: date
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showReminderDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = nextReminderAt ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showReminderDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    nextReminderAt = datePickerState.selectedDateMillis
                    showReminderDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
