package com.dites.dinolog.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dites.dinolog.data.local.entity.ScuteLogEntity
import com.dites.dinolog.data.local.entity.ScutePhotoEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModel
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScuteLogScreen(
    reptileId: Long,
    logId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    viewModel: ReptileDetailViewModel = viewModel(
        factory = ReptileDetailViewModelFactory(repository, reptileId)
    )
) {
    val context = LocalContext.current
    val scuteLogs by viewModel.scuteLogs.collectAsState()
    val log = scuteLogs.find { it.id == logId }
    val existingPhotos by viewModel.getPhotosForScuteLog(logId).collectAsState(initial = emptyList())

    var recordedAt by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var condition by remember { mutableStateOf("NORMAL") }
    var notes by remember { mutableStateOf("") }
    
    val newPhotoUris = remember { mutableStateListOf<String>() }
    val photosToDelete = remember { mutableStateListOf<ScutePhotoEntity>() }

    LaunchedEffect(log) {
        log?.let {
            recordedAt = it.recordedAt
            condition = it.condition
            notes = it.notes
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var cameraPhotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraPhotoUri != null && (existingPhotos.size - photosToDelete.size + newPhotoUris.size) < 4) {
            newPhotoUris.add(cameraPhotoUri.toString())
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && (existingPhotos.size - photosToDelete.size + newPhotoUris.size) < 4) {
            val savedUri = saveScuteImageToInternalStorage(context, uri)
            if (savedUri != null) {
                newPhotoUris.add(savedUri.toString())
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val tempFile = File(context.filesDir, "scute_photos/temp_${System.currentTimeMillis()}.jpg")
            tempFile.parentFile?.mkdirs()
            val uri = FileProvider.getUriForFile(
                context,
                "com.dites.dinolog.fileprovider",
                tempFile
            )
            cameraPhotoUri = uri
            cameraLauncher.launch(uri)
        }
    }

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
                title = { Text("Edit Kondisi Karapas") },
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

            Text(text = "Foto Karapas (maks. 4)", style = MaterialTheme.typography.labelLarge)
            
            val totalPhotos = existingPhotos.size - photosToDelete.size + newPhotoUris.size
            if (totalPhotos < 4) {
                Button(
                    onClick = { showPhotoOptions = true }
                ) {
                    Text("Tambah Foto")
                }
            }

            if (existingPhotos.isNotEmpty() || newPhotoUris.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show existing photos that are not marked for deletion
                    items(existingPhotos.filter { it !in photosToDelete }) { photo ->
                        Box(modifier = Modifier.size(64.dp)) {
                            AsyncImage(
                                model = photo.photoUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { photosToDelete.add(photo) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                ) {
                                    Text(
                                        "×",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    // Show new photos
                    items(newPhotoUris) { uri ->
                        Box(modifier = Modifier.size(64.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { newPhotoUris.remove(uri) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                ) {
                                    Text(
                                        "×",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    log?.let { current ->
                        viewModel.updateScuteLog(
                            current.copy(
                                recordedAt = recordedAt,
                                condition = condition,
                                notes = notes
                            )
                        )
                        // Handle photo changes
                        photosToDelete.forEach { viewModel.deleteScutePhoto(it) }
                        if (newPhotoUris.isNotEmpty()) {
                            val newPhotos = newPhotoUris.map { ScutePhotoEntity(scuteLogId = logId, photoUri = it) }
                            viewModel.addScutePhotos(newPhotos)
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
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
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

private fun saveScuteImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "scute_photos/scute_${System.currentTimeMillis()}.jpg")
        file.parentFile?.mkdirs()
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
