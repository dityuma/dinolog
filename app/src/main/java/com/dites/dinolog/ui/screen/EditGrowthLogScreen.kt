package com.dites.dinolog.ui.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dites.dinolog.data.local.entity.GrowthLogEntity
import com.dites.dinolog.data.local.entity.GrowthPhotoEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModel
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditGrowthLogScreen(
    reptileId: Long,
    logId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    viewModel: ReptileDetailViewModel = viewModel(
        factory = ReptileDetailViewModelFactory(repository, reptileId)
    )
) {
    val context = LocalContext.current
    val logs by viewModel.growthLogs.collectAsState()
    val log = logs.find { it.id == logId }
    val existingPhotos by viewModel.getPhotosForLog(logId).collectAsState(initial = emptyList())

    var recordedAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var weightGrams by remember { mutableStateOf("") }
    var lengthCm by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Use a local list for newly added photos before saving
    var newPhotos by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(log) {
        log?.let {
            recordedAt = it.recordedAt
            weightGrams = it.weightGrams?.toString() ?: ""
            lengthCm = it.lengthCm?.toString() ?: ""
            notes = it.notes
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedUri = saveGrowthImageToInternalStorage(context, it)
            if (savedUri != null && (existingPhotos.size + newPhotos.size) < 4) {
                newPhotos = newPhotos + savedUri.toString()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempPhotoUri?.let {
                if ((existingPhotos.size + newPhotos.size) < 4) {
                    newPhotos = newPhotos + it.toString()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Catatan Pertumbuhan") },
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

            OutlinedTextField(
                value = weightGrams,
                onValueChange = { weightGrams = it },
                label = { Text("Berat (gram)") },
                placeholder = { Text("e.g. 85") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = lengthCm,
                onValueChange = { lengthCm = it },
                label = { Text("Panjang (cm)") },
                placeholder = { Text("e.g. 18.5") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan") },
                placeholder = { Text("Catatan tambahan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text(text = "Foto Dokumentasi (maks. 4)", style = MaterialTheme.typography.labelLarge)
            
            Button(
                onClick = { showPhotoOptions = true },
                enabled = (existingPhotos.size + newPhotos.size) < 4
            ) {
                Text("Tambah Foto")
            }

            if (existingPhotos.isNotEmpty() || newPhotos.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    existingPhotos.forEach { photo ->
                        Box {
                            AsyncImage(
                                model = photo.photoUri,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.deletePhoto(photo) },
                                modifier = Modifier.size(24.dp).align(Alignment.TopEnd)
                            ) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.error) {
                                    Text("×", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                    newPhotos.forEach { uri ->
                        Box {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { newPhotos = newPhotos - uri },
                                modifier = Modifier.size(24.dp).align(Alignment.TopEnd)
                            ) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.error) {
                                    Text("×", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    log?.let { current ->
                        viewModel.updateGrowthLog(
                            current.copy(
                                recordedAt = recordedAt,
                                weightGrams = weightGrams.toFloatOrNull(),
                                lengthCm = lengthCm.toFloatOrNull(),
                                notes = notes
                            )
                        )
                        // Save new photos
                        newPhotos.forEach { uri ->
                            viewModel.addPhotoToLog(logId, uri)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Perubahan")
            }
        }
    }

    if (showPhotoOptions) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoOptions = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Tambah Foto",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                ListItem(
                    headlineContent = { Text("Ambil Foto") },
                    leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showPhotoOptions = false
                        val uri = createGrowthTempPictureUri(context)
                        tempPhotoUri = uri
                        cameraLauncher.launch(uri)
                    }
                )
                ListItem(
                    headlineContent = { Text("Pilih dari Galeri") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showPhotoOptions = false
                        galleryLauncher.launch("image/*")
                    }
                )
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

private fun createGrowthTempPictureUri(context: Context): Uri {
    val directory = File(context.filesDir, "growth_photos")
    if (!directory.exists()) directory.mkdirs()
    val file = File(directory, "temp_growth_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "com.dites.dinolog.fileprovider",
        file
    )
}

private fun saveGrowthImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val directory = File(context.filesDir, "growth_photos")
        if (!directory.exists()) directory.mkdirs()
        val file = File(directory, "growth_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        FileProvider.getUriForFile(
            context,
            "com.dites.dinolog.fileprovider",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
