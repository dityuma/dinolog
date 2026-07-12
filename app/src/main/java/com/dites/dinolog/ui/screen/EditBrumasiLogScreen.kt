package com.dites.dinolog.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.TortoiseCareViewModel
import com.dites.dinolog.ui.viewmodel.TortoiseCareViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBrumasiLogScreen(
    reptileId: Long,
    logId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    viewModel: TortoiseCareViewModel = viewModel(
        factory = TortoiseCareViewModelFactory(repository, reptileId)
    )
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val brumasiLogs by viewModel.brumasiLogs.collectAsState()
    val log = brumasiLogs.find { it.id == logId }

    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var isOngoing by remember { mutableStateOf(true) }
    var weightBeforeGrams by remember { mutableStateOf("") }
    var weightAfterGrams by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(log) {
        if (log != null && !initialized) {
            startDate = log.startDate
            endDate = log.endDate
            isOngoing = log.endDate == null
            weightBeforeGrams = log.weightBeforeGrams?.toString() ?: ""
            weightAfterGrams = log.weightAfterGrams?.toString() ?: ""
            notes = log.notes
            initialized = true
        }
    }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Catatan Brumasi") },
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
                value = dateFormatter.format(Date(startDate)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Mulai Brumasi") },
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isOngoing, onCheckedChange = { 
                    isOngoing = it 
                    if (it) endDate = null
                })
                Text("Brumasi masih berlangsung")
            }

            if (!isOngoing) {
                OutlinedTextField(
                    value = endDate?.let { dateFormatter.format(Date(it)) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tanggal Sembuh Brumasi") },
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = weightBeforeGrams,
                onValueChange = { weightBeforeGrams = it },
                label = { Text("Berat Sebelum Brumasi (gram, opsional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("e.g. 850") }
            )

            if (!isOngoing) {
                OutlinedTextField(
                    value = weightAfterGrams,
                    onValueChange = { weightAfterGrams = it },
                    label = { Text("Berat Setelah Brumasi (gram, opsional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("e.g. 820") }
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
                    log?.let { current ->
                        scope.launch {
                            viewModel.updateBrumasiLog(
                                current.copy(
                                    startDate = startDate,
                                    endDate = if (isOngoing) null else endDate,
                                    weightBeforeGrams = weightBeforeGrams.toFloatOrNull(),
                                    weightAfterGrams = weightAfterGrams.toFloatOrNull(),
                                    notes = notes
                                )
                            )
                            snackbarHostState.showSnackbar("Catatan brumasi berhasil diperbarui")
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
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
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDate = datePickerState.selectedDateMillis
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
