package com.dites.dinolog.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.dites.dinolog.data.local.dao.LengthPoint
import com.dites.dinolog.data.local.dao.WeightPoint
import com.dites.dinolog.data.local.entity.*
import com.dites.dinolog.data.repository.DinoLogRepository
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModel
import com.dites.dinolog.ui.viewmodel.ReptileDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReptileDetailScreen(
    reptileId: Long,
    repository: DinoLogRepository,
    onNavigateBack: () -> Unit,
    onNavigateToAddGrowth: (Long) -> Unit,
    onNavigateToEditGrowth: (Long, Long) -> Unit,
    onNavigateToAddFeeding: (Long) -> Unit,
    onNavigateToEditFeeding: (Long, Long) -> Unit,
    onNavigateToAddScute: (Long) -> Unit,
    onNavigateToAddRiwayat: (Long) -> Unit,
    onNavigateToEditRiwayat: (Long, Long) -> Unit,
    onNavigateToAddBrumasi: (Long) -> Unit,
    onNavigateToEditReptile: (Long) -> Unit,
    viewModel: ReptileDetailViewModel = viewModel(
        factory = ReptileDetailViewModelFactory(repository, reptileId)
    )
) {
    val reptile by viewModel.reptile.collectAsState()
    val growthLogs by viewModel.growthLogs.collectAsState()
    val feedingLogs by viewModel.feedingLogs.collectAsState()
    val scuteLogs by viewModel.scuteLogs.collectAsState()
    val riwayatLogs by viewModel.riwayatLogs.collectAsState()
    val brumasiLogs by viewModel.brumasiLogs.collectAsState()
    val weightHistory by viewModel.weightHistory.collectAsState()
    val lengthHistory by viewModel.lengthHistory.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var previewPhotoUri by remember { mutableStateOf<String?>(null) }
    val tabs = listOf("Tumbuh", "Makan", "Karapas", "Riwayat", "Brumasi")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(reptile?.name ?: "Detail Reptil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            val tooltip = when (selectedTabIndex) {
                0 -> "Tambah Catatan Tumbuh"
                1 -> "Tambah Catatan Makan"
                2 -> "Tambah Kondisi Karapas"
                3 -> "Tambah Riwayat Sakit"
                4 -> "Tambah Catatan Brumasi"
                else -> "Tambah Record"
            }
            FloatingActionButton(onClick = {
                when (selectedTabIndex) {
                    0 -> onNavigateToAddGrowth(reptileId)
                    1 -> onNavigateToAddFeeding(reptileId)
                    2 -> onNavigateToAddScute(reptileId)
                    3 -> onNavigateToAddRiwayat(reptileId)
                    4 -> onNavigateToAddBrumasi(reptileId)
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = tooltip)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            reptile?.let { r ->
                val latestWeight = growthLogs.firstOrNull { it.weightGrams != null }?.weightGrams
                val latestLength = growthLogs.firstOrNull { it.lengthCm != null }?.lengthCm
                ReptileHeader(
                    r,
                    growthLogs.size,
                    latestWeight,
                    latestLength,
                    onPhotoClick = { if (r.profilePhotoUri.isNotEmpty()) previewPhotoUri = r.profilePhotoUri },
                    onEditClick = { onNavigateToEditReptile(reptileId) }
                )
            }

            ScrollableTabRow(selectedTabIndex = selectedTabIndex, edgePadding = 16.dp) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> GrowthTab(growthLogs, weightHistory, lengthHistory, viewModel, { uri -> previewPhotoUri = uri }) { logId ->
                    onNavigateToEditGrowth(reptileId, logId)
                }
                1 -> FeedingTab(feedingLogs) { logId ->
                    onNavigateToEditFeeding(reptileId, logId)
                }
                2 -> ScuteTab(scuteLogs, viewModel) { uri -> previewPhotoUri = uri }
                3 -> RiwayatTab(riwayatLogs) { riwayatId ->
                    onNavigateToEditRiwayat(reptileId, riwayatId)
                }
                4 -> BrumasiTab(brumasiLogs)
            }
        }
    }

    previewPhotoUri?.let { uri ->
        Dialog(
            onDismissRequest = { previewPhotoUri = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable { previewPhotoUri = null }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun ReptileHeader(
    reptile: ReptileEntity,
    logCount: Int,
    latestWeight: Float?,
    latestLength: Float?,
    onPhotoClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onEditClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .alpha(0f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = reptile.profilePhotoUri.takeIf { it.isNotEmpty() },
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .clickable { onPhotoClick() },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = reptile.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(text = reptile.species, style = MaterialTheme.typography.bodyLarge)
                        val genderIndo = when(reptile.gender) {
                            "MALE" -> "Jantan"
                            "FEMALE" -> "Betina"
                            else -> "Tidak Diketahui"
                        }
                        Text(text = "Jenis Kelamin: $genderIndo", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Umur: ${calculateDuration(reptile.birthDate)}", style = MaterialTheme.typography.bodySmall)
                        
                        reptile.acquireDate?.let { date ->
                            val acquireFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
                            Text(
                                text = "Adopsi: ${acquireFormatter.format(Date(date))} (${calculateDuration(date)})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(label = "Total Catatan", value = logCount.toString())
                    StatItem(label = "Berat Terkini", value = latestWeight?.let { "${it} gram" } ?: "-")
                    StatItem(label = "Panjang Terkini", value = latestLength?.let { "${it} cm" } ?: "-")
                }
            }
        }
    }
}
@Composable
fun LogPhotoRow(
    photos: List<String>,
    onPhotoClick: (String) -> Unit
) {
    if (photos.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            photos.forEach { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPhotoClick(uri) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun GrowthTab(
    logs: List<GrowthLogEntity>,
    weightHistory: List<WeightPoint>,
    lengthHistory: List<LengthPoint>,
    viewModel: ReptileDetailViewModel,
    onPhotoClick: (String) -> Unit,
    onEditLog: (Long) -> Unit
) {
    var showWeight by remember { mutableStateOf(true) }
    val modelProducer = remember { CartesianChartModelProducer() }
    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    val currentHistory = if (showWeight) weightHistory else lengthHistory
    val hasEnoughData = currentHistory.size >= 2

    LaunchedEffect(weightHistory, lengthHistory, showWeight) {
        if (hasEnoughData) {
            modelProducer.runTransaction {
                lineSeries {
                    series(currentHistory.map { point ->
                        if (point is WeightPoint) point.weightGrams else (point as LengthPoint).lengthCm
                    })
                }
            }
        }
    }

    val fullDateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                SegmentedButton(
                    selected = showWeight,
                    onClick = { showWeight = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Berat (g)")
                }
                SegmentedButton(
                    selected = !showWeight,
                    onClick = { showWeight = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Panjang (cm)")
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                if (hasEnoughData) {
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            rememberLineCartesianLayer(),
                            startAxis = VerticalAxis.rememberStart(),
                            bottomAxis = HorizontalAxis.rememberBottom(
                                valueFormatter = CartesianValueFormatter { _, value, _ ->
                                    val index = value.toInt()
                                    val timestamp = if (showWeight) {
                                        weightHistory.getOrNull(index)?.recordedAt
                                    } else {
                                        lengthHistory.getOrNull(index)?.recordedAt
                                    }
                                    timestamp?.let { dateFormatter.format(Date(it)) } ?: ""
                                }
                            )
                        ),
                        modelProducer = modelProducer,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Tambahkan 2 catatan atau lebih untuk melihat grafik pertumbuhan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        items(logs) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditLog(log.id) }
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.TopEnd)
                            .alpha(0f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column {
                        Text(text = fullDateFormatter.format(Date(log.recordedAt)), style = MaterialTheme.typography.labelMedium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            log.weightGrams?.let { Text(text = "${it}g", fontWeight = FontWeight.Bold) }
                            log.lengthCm?.let { Text(text = "${it}cm", fontWeight = FontWeight.Bold) }
                        }
                        if (log.notes.isNotEmpty()) {
                            Text(text = log.notes, style = MaterialTheme.typography.bodyMedium)
                        }
                        
                        val photos by viewModel.getPhotosForLog(log.id).collectAsState(initial = emptyList())
                        LogPhotoRow(photos = photos.map { it.photoUri }, onPhotoClick = onPhotoClick)
                    }
                }
            }
        }
    }
}

@Composable
fun FeedingTab(logs: List<FeedingLogEntity>, onEditLog: (Long) -> Unit) {
    if (logs.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Belum ada catatan makan",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Ketuk + untuk menambahkan catatan makan pertama",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        return
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditLog(log.id) }
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.TopEnd)
                            .alpha(0f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column {
                        Text(text = dateFormatter.format(Date(log.feedingDate)), style = MaterialTheme.typography.labelMedium)
                        Text(text = log.foodType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (log.foodAmount.isNotEmpty()) {
                            Text(text = log.foodAmount, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (log.notes.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(text = log.notes, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScuteTab(
    logs: List<ScuteLogEntity>,
    viewModel: ReptileDetailViewModel,
    onPhotoClick: (String) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    if (logs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada catatan kondisi karapas")
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs) { log ->
            val color = when (log.condition) {
                "PIRAMIDING" -> Color(0xFFFFF3E0) // Warning Orange light
                "SOFT_SHELL" -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
            val contentColor = when (log.condition) {
                "PIRAMIDING" -> Color(0xFFE65100) // Warning Orange deep
                "SOFT_SHELL" -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = color)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = dateFormatter.format(Date(log.recordedAt)), style = MaterialTheme.typography.labelMedium)
                    val conditionIndo = when(log.condition) {
                        "NORMAL" -> "Normal"
                        "PIRAMIDING" -> "Piramiding"
                        "SOFT_SHELL" -> "Shell Lunak"
                        "RETAK" -> "Retak / Luka"
                        "JAMUR" -> "Jamur / Bercak"
                        else -> log.condition
                    }
                    Text(
                        text = "Kondisi: $conditionIndo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    if (log.notes.isNotEmpty()) {
                        Text(text = log.notes, color = contentColor)
                    }

                    val photos by viewModel.getPhotosForScuteLog(log.id).collectAsState(initial = emptyList())
                    LogPhotoRow(photos = photos.map { it.photoUri }, onPhotoClick = onPhotoClick)
                }
            }
        }
    }
}

@Composable
fun CareTab(
    soakingLogs: List<SoakingLogEntity>,
    uvbLogs: List<UvbBasingLogEntity>,
    dietLogs: List<DietLogEntity>,
    onAddSoaking: () -> Unit,
    onAddUvb: () -> Unit,
    onAddDiet: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section Soaking
        item {
            CareSectionHeader("Soaking (Mandi)", onAddSoaking)
            if (soakingLogs.isEmpty()) {
                Text("Belum ada catatan soaking", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
        }
        items(soakingLogs.take(5)) { log ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(dateFormatter.format(Date(log.soakingDate)), style = MaterialTheme.typography.labelSmall)
                    Text("Durasi: ${log.durationMinutes} menit", fontWeight = FontWeight.Bold)
                    log.waterTempCelsius?.let { Text("Suhu Air: $it°C", style = MaterialTheme.typography.bodySmall) }
                    if (log.notes.isNotEmpty()) Text(log.notes, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Section UVB
        item {
            CareSectionHeader("UVB & Basking", onAddUvb)
            if (uvbLogs.isEmpty()) {
                Text("Belum ada catatan UVB", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
        }
        items(uvbLogs.take(5)) { log ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(dateFormatter.format(Date(log.sessionDate)), style = MaterialTheme.typography.labelSmall)
                    Text("${log.uvbType} (${log.durationMinutes} menit)", fontWeight = FontWeight.Bold)
                    log.basikingTempCelsius?.let { Text("Suhu Basking: $it°C", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }

        // Section Diet
        item {
            CareSectionHeader("Diet & Sayuran", onAddDiet)
            if (dietLogs.isEmpty()) {
                Text("Belum ada catatan diet", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
        }
        items(dietLogs.take(5)) { log ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(dateFormatter.format(Date(log.recordedAt)), style = MaterialTheme.typography.labelSmall)
                    Text("Sayur: ${log.vegetables}", fontWeight = FontWeight.Bold)
                    if (log.fruits.isNotEmpty()) Text("Buah: ${log.fruits}", style = MaterialTheme.typography.bodySmall)
                    if (log.supplements.isNotEmpty()) Text("Suplemen: ${log.supplements}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun CareSectionHeader(title: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        TextButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Tambah")
        }
    }
}

@Composable
fun BrumasiTab(logs: List<BrumasiLogEntity>) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    if (logs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada catatan brumasi")
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs) { log ->
            val isActive = log.endDate == null
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = if (isActive) CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)) else CardDefaults.cardColors()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = if (isActive) "Sedang Brumasi" else "Selesai Brumasi",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isActive) Color(0xFFE65100) else MaterialTheme.colorScheme.primary
                        )
                        Text(text = log.location, style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${dateFormatter.format(Date(log.startDate))} — ${log.endDate?.let { dateFormatter.format(Date(it)) } ?: "Berlangsung"}",
                        fontWeight = FontWeight.Bold
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        log.weightBeforeGrams?.let { Text("Awal: ${it}g", style = MaterialTheme.typography.bodySmall) }
                        log.weightAfterGrams?.let { Text("Akhir: ${it}g", style = MaterialTheme.typography.bodySmall) }
                    }
                    if (log.notes.isNotEmpty()) Text(log.notes, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun RiwayatTab(logs: List<RiwayatEntity>, onEditRiwayat: (Long) -> Unit) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    if (logs.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.HealthAndSafety,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Belum ada riwayat sakit",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Ketuk + untuk menambahkan riwayat",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs) { riwayat ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditRiwayat(riwayat.id) }
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.TopEnd)
                            .alpha(0f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = riwayat.illnessName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (riwayat.isOngoing) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Text(
                                        text = "Sedang Sakit",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        Text(
                            text = dateFormatter.format(Date(riwayat.startDate)),
                            style = MaterialTheme.typography.labelMedium
                        )
                        if (!riwayat.isOngoing && riwayat.endDate != null) {
                            Text(
                                text = "Sembuh: ${dateFormatter.format(Date(riwayat.endDate))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        if (riwayat.notes.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = riwayat.notes,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

fun calculateDuration(fromDate: Long?): String {
    if (fromDate == null) return "-"
    val birth = Calendar.getInstance().apply { timeInMillis = fromDate }
    val now = Calendar.getInstance()
    
    if (now.before(birth)) return "Baru Lahir"
    
    var years = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
    var months = now.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
    var days = now.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH)
    
    if (days < 0) {
        months--
        val prevMonth = Calendar.getInstance().apply { 
            timeInMillis = now.timeInMillis
            add(Calendar.MONTH, -1) 
        }
        days += prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    if (months < 0) {
        years--
        months += 12
    }
    
    return buildString {
        if (years > 0) append("${years} thn ")
        if (months > 0) append("${months} bln ")
        if (days > 0) append("${days} h")
        if (years == 0 && months == 0 && days == 0) append("Hari ini")
    }.trim()
}
