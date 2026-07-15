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
//import com.dites.dinolog.data.local.entity.UvbBasingLogEntity
//import com.dites.dinolog.data.repository.DinoLogRepository
//import com.dites.dinolog.ui.viewmodel.TortoiseCareViewModel
//import com.dites.dinolog.ui.viewmodel.TortoiseCareViewModelFactory
//import java.text.SimpleDateFormat
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddUvbBasingLogScreen(
//    reptileId: Long,
//    repository: DinoLogRepository,
//    onNavigateBack: () -> Unit,
//    viewModel: TortoiseCareViewModel = viewModel(
//        factory = TortoiseCareViewModelFactory(repository, reptileId)
//    )
//) {
//    var sessionDate by remember { mutableStateOf(System.currentTimeMillis()) }
//    var durationMinutes by remember { mutableStateOf("") }
//    var uvbType by remember { mutableStateOf("") }
//    var basikingTempCelsius by remember { mutableStateOf("") }
//    var notes by remember { mutableStateOf("") }
//    var showDatePicker by remember { mutableStateOf(false) }
//
//    val uvbPresets = listOf("Sinar Matahari Langsung", "UVB 10.0", "UVB 5.0", "UVB 2.0", "Lampu Pijar Biasa")
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Tambah Sesi UVB & Basking") },
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
//                value = dateFormatter.format(Date(sessionDate)),
//                onValueChange = {},
//                readOnly = true,
//                label = { Text("Tanggal Sesi") },
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
//                placeholder = { Text("e.g. 30") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//
//            var expanded by remember { mutableStateOf(false) }
//            ExposedDropdownMenuBox(
//                expanded = expanded,
//                onExpandedChange = { expanded = !expanded }
//            ) {
//                OutlinedTextField(
//                    value = uvbType,
//                    onValueChange = { uvbType = it },
//                    label = { Text("Jenis UVB / Sumber Cahaya") },
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth()
//                )
//                ExposedDropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    uvbPresets.forEach { preset ->
//                        DropdownMenuItem(
//                            text = { Text(preset) },
//                            onClick = {
//                                uvbType = preset
//                                expanded = false
//                            }
//                        )
//                    }
//                }
//            }
//
//            OutlinedTextField(
//                value = basikingTempCelsius,
//                onValueChange = { basikingTempCelsius = it },
//                label = { Text("Suhu Basking Spot (°C, opsional)") },
//                placeholder = { Text("e.g. 40") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//
//            OutlinedTextField(
//                value = notes,
//                onValueChange = { notes = it },
//                label = { Text("Catatan") },
//                modifier = Modifier.fillMaxWidth(),
//                minLines = 3
//            )
//
//            Button(
//                onClick = {
//                    val duration = durationMinutes.toIntOrNull() ?: 0
//                    if (duration > 0) {
//                        viewModel.addUvbLog(
//                            UvbBasingLogEntity(
//                                reptileId = reptileId,
//                                sessionDate = sessionDate,
//                                durationMinutes = duration,
//                                uvbType = uvbType,
//                                basikingTempCelsius = basikingTempCelsius.toFloatOrNull(),
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
//        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = sessionDate)
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                TextButton(onClick = {
//                    sessionDate = datePickerState.selectedDateMillis ?: sessionDate
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
