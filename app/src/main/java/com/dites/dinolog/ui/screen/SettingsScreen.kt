package com.dites.dinolog.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dites.dinolog.BuildConfig
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.SettingsViewModel
import com.dites.dinolog.ui.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    repository: DinoLogRepository,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(repository, LocalContext.current.applicationContext)
    )
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showImportDialog by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            showImportDialog = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // SECTION 1: DATA & BACKUP
            SettingsSection(title = "DATA & BACKUP") {
                SettingsItem(
                    icon = Icons.Default.Upload,
                    title = "Export Data",
                    subtitle = "Simpan semua data ke file JSON",
                    enabled = !isLoading,
                    onClick = {
                        viewModel.exportData(
                            onSuccess = { fileName ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("✓ Data berhasil diekspor ke folder Downloads: $fileName")
                                }
                            },
                            onError = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Export gagal. Coba lagi.")
                                }
                            }
                        )
                    }
                )
                SettingsItem(
                    icon = Icons.Default.Download,
                    title = "Import Data",
                    subtitle = "Pulihkan data dari file JSON",
                    enabled = !isLoading,
                    onClick = { importLauncher.launch("application/json") }
                )
            }

            // SECTION 2: TENTANG APLIKASI
            SettingsSection(title = "TENTANG APLIKASI") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Versi Aplikasi",
                    subtitle = BuildConfig.VERSION_NAME,
                    clickable = false
                )
                SettingsItem(
                    icon = Icons.Default.Email,
                    title = "Hubungi Developer",
                    subtitle = "Kirim saran atau laporan bug",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:ditesodin@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "DinoLog - Feedback")
                        }
                        context.startActivity(Intent.createChooser(intent, "Kirim Email"))
                    }
                )
                SettingsItem(
                    icon = Icons.Default.Code,
                    title = "Source Code",
                    subtitle = "Lihat kode sumber di GitHub",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dityuma/dinolog"))
                        context.startActivity(intent)
                    }
                )
            }

            // SECTION 3: DUKUNG DEVELOPER
            SettingsSection(title = "DUKUNG DEVELOPER") {
                SupportCard(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://saweria.co/ditesodin"))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }

    if (isLoading) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text("Memproses...") },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        )
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Impor Data?") },
            text = { Text("Data yang ada akan digabung dengan data dari file backup. Lanjutkan?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportDialog = false
                        selectedUri?.let { uri ->
                            viewModel.importData(
                                uri = uri,
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("✓ Data berhasil diimpor")
                                    }
                                },
                                onError = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Import gagal. Pastikan file JSON valid.")
                                    }
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Impor")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(content = content)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    clickable: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(title, color = MaterialTheme.colorScheme.onSurface) },
        supportingContent = { Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = if (clickable) Modifier.clickable(enabled = enabled, onClick = onClick) else Modifier
    )
}

@Composable
fun SupportCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("☕", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Dukung DinoLog",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W500,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Jika DinoLog bermanfaat untukmu, kamu bisa mendukung pengembangan app ini dengan donasi kecil. Terima kasih! 🐢",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("☕ Donasi via Saweria")
            }
        }
    }
}
