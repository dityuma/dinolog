package com.dites.dinolog.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.dites.dinolog.data.local.entity.ReptileEntity
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModel
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReptileScreen(
    reptileId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    viewModel: ReptileDetailViewModel = viewModel(
        factory = ReptileDetailViewModelFactory(repository, reptileId)
    )
) {
    val context = LocalContext.current
    val reptile by viewModel.reptile.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("TIDAK_DIKETAHUI") }
    var birthDate by remember { mutableStateOf<Long?>(null) }
    var acquireDate by remember { mutableStateOf<Long?>(null) }
    var profilePhotoUri by remember { mutableStateOf("") }

    // Initialize state when reptile data is loaded
    LaunchedEffect(reptile) {
        reptile?.let {
            name = it.name
            species = it.species
            gender = it.gender
            birthDate = it.birthDate
            acquireDate = it.acquireDate
            profilePhotoUri = it.profilePhotoUri
        }
    }

    var showPhotoOptions by remember { mutableStateOf(false) }
    var cameraPhotoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val cameraPhotoUri = cameraPhotoUriString?.toUri()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedUri = saveEditImageToInternalStorage(context, it)
            if (savedUri != null) {
                profilePhotoUri = savedUri.toString()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraPhotoUri?.let {
                profilePhotoUri = it.toString()
            }
        }
        cameraPhotoUriString = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createEditTempPictureUri(context)
            cameraPhotoUriString = uri.toString()
            cameraLauncher.launch(uri)
        }
    }

    var showBirthDatePicker by remember { mutableStateOf(false) }
    var showAcquireDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Kura-kura") },
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
            // Photo Picker
            Text(text = "Foto Profil", style = MaterialTheme.typography.labelLarge)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { showPhotoOptions = true },
                contentAlignment = Alignment.Center
            ) {
                if (profilePhotoUri.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Pilih Foto", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                } else {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(profilePhotoUri)
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
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama") },
                placeholder = { Text("e.g. Mochi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = name.isBlank(),
                supportingText = { if (name.isBlank()) Text("Nama wajib diisi") }
            )

            OutlinedTextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Spesies") },
                placeholder = { Text("e.g. Sulcata") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = species.isBlank(),
                supportingText = { if (species.isBlank()) Text("Spesies wajib diisi") }
            )

            // Gender Dropdown
            var expanded by remember { mutableStateOf(false) }
            val genderOptions = listOf(
                "MALE" to "Jantan",
                "FEMALE" to "Betina",
                "UNKNOWN" to "Tidak Diketahui"
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = genderOptions.find { it.first == gender }?.second ?: "Tidak Diketahui",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Jenis Kelamin") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    genderOptions.forEach { selection ->
                        DropdownMenuItem(
                            text = { Text(selection.second) },
                            onClick = {
                                gender = selection.first
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Date Pickers
            val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

            OutlinedTextField(
                value = birthDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Lahir (opsional)") },
                trailingIcon = {
                    IconButton(onClick = { showBirthDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = acquireDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Adopsi / Beli") },
                trailingIcon = {
                    IconButton(onClick = { showAcquireDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (name.isNotBlank() && species.isNotBlank()) {
                        reptile?.let { current ->
                            viewModel.updateReptile(
                                current.copy(
                                    name = name,
                                    species = species,
                                    gender = gender,
                                    birthDate = birthDate,
                                    acquireDate = acquireDate,
                                    profilePhotoUri = profilePhotoUri
                                )
                            )
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && species.isNotBlank()
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
                    text = "Pilih Foto Profil",
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

    if (showBirthDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthDate)
        DatePickerDialog(
            onDismissRequest = { showBirthDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthDate = datePickerState.selectedDateMillis
                    showBirthDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showAcquireDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = acquireDate)
        DatePickerDialog(
            onDismissRequest = { showAcquireDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    acquireDate = datePickerState.selectedDateMillis
                    showAcquireDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun createEditTempPictureUri(context: Context): Uri {
    val tempFile = File(context.filesDir, "profile_photos/temp_${System.currentTimeMillis()}.jpg")
    tempFile.parentFile?.mkdirs()
    return FileProvider.getUriForFile(
        context,
        "com.dites.dinolog.fileprovider",
        tempFile
    )
}

private fun saveEditImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "profile_photos/profile_${System.currentTimeMillis()}.jpg")
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
