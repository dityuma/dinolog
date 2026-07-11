package com.dites.dinolog.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dites.dinolog.data.local.entity.RiwayatEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModel
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRiwayatScreen(
    reptileId: Long,
    riwayatId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    viewModel: ReptileDetailViewModel = viewModel(
        factory = ReptileDetailViewModelFactory(repository, reptileId)
    )
) {
    val riwayatLogs by viewModel.riwayatLogs.collectAsState()
    val riwayat = riwayatLogs.find { it.id == riwayatId }

    var illnessName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var isOngoing by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf(System.currentTimeMillis()) }

    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(riwayat) {
        if (riwayat != null && !initialized) {
            illnessName = riwayat.illnessName
            notes = riwayat.notes
            startDate = riwayat.startDate
            isOngoing = riwayat.isOngoing
            endDate = riwayat.endDate ?: System.currentTimeMillis()
            initialized = true
        }
    }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Riwayat Sakit") },
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
            OutlinedTextField(
                value = illnessName,
                onValueChange = { illnessName = it },
                label = { Text("Riwayat Sakit") },
                placeholder = { Text("e.g. Infeksi Saluran Pernapasan") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan Detail") },
                placeholder = { Text("e.g. Gejala, obat yang diberikan, dll") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = dateFormatter.format(Date(startDate)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Mulai Sakit") },
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal Mulai")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isOngoing,
                    onCheckedChange = { isOngoing = it }
                )
                Text(text = "Sakit masih berlangsung")
            }

            if (!isOngoing) {
                OutlinedTextField(
                    value = dateFormatter.format(Date(endDate)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tanggal Sembuh") },
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal Sembuh")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    riwayat?.let {
                        viewModel.updateRiwayat(
                            it.copy(
                                illnessName = illnessName,
                                notes = notes,
                                startDate = startDate,
                                isOngoing = isOngoing,
                                endDate = if (isOngoing) null else endDate
                            )
                        )
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = illnessName.isNotBlank()
            ) {
                Text("Simpan Perubahan")
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDate = datePickerState.selectedDateMillis ?: startDate
                    showStartDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate)
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDate = datePickerState.selectedDateMillis ?: endDate
                    showEndDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
