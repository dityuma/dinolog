package com.dites.dinolog.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.BrokenImage
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.dites.dinolog.data.local.entity.RiwayatEntity
import com.dites.dinolog.data.local.entity.RiwayatPhotoEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModel
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModelFactory
import java.io.File
import java.io.FileOutputStream
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
    val context = LocalContext.current
    val riwayatLogs by viewModel.riwayatLogs.collectAsStateWithLifecycle()
    val riwayat = riwayatLogs.find { it.id == riwayatId }
    val existingPhotos by viewModel.getPhotosForRiwayat(riwayatId).collectAsStateWithLifecycle(initialValue = emptyList())

    var illnessName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isOngoing by remember { mutableStateOf(false) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val newPhotoUris = remember { mutableStateListOf<String>() }
    val photosToDelete = remember { mutableStateListOf<RiwayatPhotoEntity>() }

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
            val savedUri = saveRiwayatImageToInternalStorage(context, uri)
            if (savedUri != null) {
                newPhotoUris.add(savedUri.toString())
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val tempFile = File(context.filesDir, "riwayat_photos/temp_${System.currentTimeMillis()}.jpg")
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
                placeholder = { Text("e.g. Metabolic Bone Disease") },
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

            Text(text = "Foto Dokumentasi (maks. 4)", style = MaterialTheme.typography.labelLarge)
            
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
                    // Show existing photos not marked for deletion
                    items(existingPhotos.filter { it !in photosToDelete }) { photo ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photo.photoUri)
                                    .crossfade(true)
                                    .size(256, 256)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    )
                                },
                                error = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.BrokenImage,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
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
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(uri)
                                    .crossfade(true)
                                    .size(256, 256)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    )
                                },
                                error = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.BrokenImage,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
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
                    riwayat?.let { current ->
                        viewModel.updateRiwayat(
                            current.copy(
                                illnessName = illnessName,
                                notes = notes,
                                startDate = startDate,
                                isOngoing = isOngoing,
                                endDate = if (isOngoing) null else endDate
                            )
                        )
                        // Handle photo deletions
                        photosToDelete.forEach { viewModel.deleteRiwayatPhoto(it) }
                        // Handle new photo additions
                        if (newPhotoUris.isNotEmpty()) {
                            val newPhotos = newPhotoUris.map { RiwayatPhotoEntity(riwayatId = riwayatId, photoUri = it) }
                            viewModel.addRiwayatPhotos(newPhotos)
                        }
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

private fun saveRiwayatImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "riwayat_photos/riwayat_${System.currentTimeMillis()}.jpg")
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
