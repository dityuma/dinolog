//package com.dites.dinolog.ui.screen
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.dites.dinolog.data.local.entity.SoakingLogEntity
//import com.dites.dinolog.data.repository.DinoLogRepository
//import com.dites.dinolog.ui.viewmodel.TortoiseCareViewModel
//import com.dites.dinolog.ui.viewmodel.TortoiseCareViewModelFactory
//import java.text.SimpleDateFormat
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddSoakingLogScreen(
//    reptileId: Long,
//    repository: DinoLogRepository,
//    onNavigateBack: () -> Unit,
//    viewModel: TortoiseCareViewModel = viewModel(
//        factory = TortoiseCareViewModelFactory(repository, reptileId)
//    )
//) {
//    var soakingDate by remember { mutableStateOf(System.currentTimeMillis()) }
//    var durationMinutes by remember { mutableStateOf("") }
//    var waterTempCelsius by remember { mutableStateOf("") }
//    var notes by remember { mutableStateOf("") }
//    var showDatePicker by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Tambah Catatan Soaking") },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState()),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
//
//            OutlinedTextField(
//                value = dateFormatter.format(Date(soakingDate)),
//                onValueChange = {},
//                readOnly = true,
//                label = { Text("Tanggal Soaking") },
//                trailingIcon = {
//                    IconButton(onClick = { showDatePicker = true }) {
//                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            OutlinedTextField(
//                value = durationMinutes,
//                onValueChange = { durationMinutes = it },
//                label = { Text("Durasi (menit)") },
//                placeholder = { Text("e.g. 20") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//
//            OutlinedTextField(
//                value = waterTempCelsius,
//                onValueChange = { waterTempCelsius = it },
//                label = { Text("Suhu Air (°C, opsional)") },
//                placeholder = { Text("e.g. 30") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//
//            OutlinedTextField(
//                value = notes,
//                onValueChange = { notes = it },
//                label = { Text("Catatan") },
//                placeholder = { Text("e.g. Kura-kura aktif minum") },
//                modifier = Modifier.fillMaxWidth(),
//                minLines = 3
//            )
//
//            Button(
//                onClick = {
//                    val duration = durationMinutes.toIntOrNull() ?: 0
//                    if (duration > 0) {
//                        viewModel.addSoakingLog(
//                            SoakingLogEntity(
//                                reptileId = reptileId,
//                                soakingDate = soakingDate,
//                                durationMinutes = duration,
//                                waterTempCelsius = waterTempCelsius.toFloatOrNull(),
//                                notes = notes
//                            )
//                        )
//                        onNavigateBack()
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                enabled = durationMinutes.isNotEmpty() && (durationMinutes.toIntOrNull() ?: 0) > 0
//            ) {
//                Text("Simpan")
//            }
//        }
//    }
//
//    if (showDatePicker) {
//        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = soakingDate)
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                TextButton(onClick = {
//                    soakingDate = datePickerState.selectedDateMillis ?: soakingDate
//                    showDatePicker = false
//                }) {
//                    Text("OK")
//                }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//}
