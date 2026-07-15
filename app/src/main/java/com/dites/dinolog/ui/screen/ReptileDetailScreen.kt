package com.dites.dinolog.ui.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
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
    onNavigateToEditScute: (Long, Long) -> Unit,
    onNavigateToAddRiwayat: (Long) -> Unit,
    onNavigateToEditRiwayat: (Long, Long) -> Unit,
    onNavigateToAddBrumasi: (Long) -> Unit,
    onNavigateToEditBrumasi: (Long, Long) -> Unit,
    onNavigateToEditReptile: (Long) -> Unit,
    viewModel: ReptileDetailViewModel = viewModel(
        factory = ReptileDetailViewModelFactory(repository, reptileId)
    )
) {
    val reptile by viewModel.reptile.collectAsStateWithLifecycle()
    val growthLogs by viewModel.growthLogs.collectAsStateWithLifecycle()
    val feedingLogs by viewModel.feedingLogs.collectAsStateWithLifecycle()
    val scuteLogs by viewModel.scuteLogs.collectAsStateWithLifecycle()
    val riwayatLogs by viewModel.riwayatLogs.collectAsStateWithLifecycle()
    val brumasiLogs by viewModel.brumasiLogs.collectAsStateWithLifecycle()
    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
    val lengthHistory by viewModel.lengthHistory.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var previewPhotoUri by remember { mutableStateOf<String?>(null) }
    val tabs = listOf("Tumbuh", "Makan", "Karapas", "Riwayat", "Brumasi")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(reptile?.name ?: "Detail Reptil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
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
            FloatingActionButton(
                onClick = {
                    when (selectedTabIndex) {
                        0 -> onNavigateToAddGrowth(reptileId)
                        1 -> onNavigateToAddFeeding(reptileId)
                        2 -> onNavigateToAddScute(reptileId)
                        3 -> onNavigateToAddRiwayat(reptileId)
                        4 -> onNavigateToAddBrumasi(reptileId)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
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
                val ageText = remember(r.birthDate) {
                    r.birthDate?.let { calculateDuration(it) } ?: "-"
                }
                val acquireDateText = remember(r.acquireDate) {
                    r.acquireDate?.let {
                        val formatted = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(it))
                        val duration = calculateDuration(it)
                        "Adopsi: $formatted ($duration)"
                    }
                }
                val latestWeight = remember(growthLogs) {
                    growthLogs.firstOrNull { it.weightGrams != null }?.weightGrams?.let { "${it.toInt()} gram" } ?: "-"
                }
                val latestLength = remember(growthLogs) {
                    growthLogs.firstOrNull { it.lengthCm != null }?.lengthCm?.let { "$it cm" } ?: "-"
                }

                ReptileHeader(
                    reptile = r,
                    latestWeight = latestWeight,
                    latestLength = latestLength,
                    ageText = ageText,
                    acquireDateText = acquireDateText,
                    onPhotoClick = { if (r.profilePhotoUri.isNotEmpty()) previewPhotoUri = r.profilePhotoUri },
                    onEditClick = { onNavigateToEditReptile(reptileId) }
                )
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTabIndex == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
                2 -> ScuteTab(scuteLogs, viewModel, { uri -> previewPhotoUri = uri }) { logId ->
                    onNavigateToEditScute(reptileId, logId)
                }
                3 -> RiwayatTab(riwayatLogs, viewModel, { uri -> previewPhotoUri = uri }) { riwayatId ->
                    onNavigateToEditRiwayat(reptileId, riwayatId)
                }
                4 -> BrumasiTab(brumasiLogs) { logId ->
                    onNavigateToEditBrumasi(reptileId, logId)
                }
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
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .crossfade(true)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.BrokenImage,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ReptileHeader(
    reptile: ReptileEntity,
    latestWeight: String,
    latestLength: String,
    ageText: String,
    acquireDateText: String?,
    onPhotoClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onEditClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onPhotoClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(reptile.profilePhotoUri.takeIf { it.isNotEmpty() })
                                .crossfade(true)
                                .size(128, 128)
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = reptile.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = reptile.species,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val genderIndo = when(reptile.gender) {
                            "MALE" -> "Jantan"
                            "FEMALE" -> "Betina"
                            else -> "Tidak Diketahui"
                        }
                        Text(
                            text = "Jenis Kelamin: $genderIndo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Umur: $ageText",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        acquireDateText?.let { text ->
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "Berat Terkini", value = latestWeight)
                    StatItem(label = "Panjang Terkini", value = latestLength)
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
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onPhotoClick(uri) }
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
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
    if (logs.isEmpty()) {
        TabEmptyState(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            title = "Belum ada catatan pertumbuhan",
            subtitle = "Tambahkan 2 catatan atau lebih untuk melihat grafik pertumbuhan"
        )
        return
    }

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

    remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
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

        items(
            items = logs,
            key = { it.id }
        ) { log ->
            GrowthLogCard(log, viewModel, onPhotoClick, onEditLog)
        }
    }
}

@Composable
fun GrowthLogCard(
    log: GrowthLogEntity,
    viewModel: ReptileDetailViewModel,
    onPhotoClick: (String) -> Unit,
    onEditLog: (Long) -> Unit
) {
    val fullDateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val photos by produceState<List<GrowthPhotoEntity>>(
        initialValue = emptyList(),
        key1 = log.id
    ) {
        viewModel.getPhotosForLog(log.id).collect { value = it }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditLog(log.id) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    log.weightGrams?.let { weight ->
                        Column {
                            Text(
                                text = "Berat",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(text = "$weight g", fontWeight = FontWeight.Bold)
                        }
                    }
                    log.lengthCm?.let { length ->
                        Column {
                            Text(
                                text = "Panjang",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(text = "$length cm", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (log.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = log.notes, style = MaterialTheme.typography.bodyMedium)
                }

                LogPhotoRow(photos = photos.map { it.photoUri }, onPhotoClick = onPhotoClick)
            }
        }
    }
}

@Composable
fun FeedingTab(logs: List<FeedingLogEntity>, onEditLog: (Long) -> Unit) {
    if (logs.isEmpty()) {
        TabEmptyState(
            icon = Icons.Default.Restaurant,
            title = "Belum ada catatan makan",
            subtitle = "Ketuk + untuk menambahkan catatan makan pertama"
        )
        return
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = logs,
            key = { it.id }
        ) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditLog(log.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
    onPhotoClick: (String) -> Unit,
    onEditLog: (Long) -> Unit
) {
    if (logs.isEmpty()) {
        TabEmptyState(
            icon = Icons.Default.Shield,
            title = "Belum ada catatan karapas",
            subtitle = "Ketuk + untuk menambahkan kondisi karapas pertama"
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = logs,
            key = { it.id }
        ) { log ->
            ScuteLogCard(log, viewModel, onPhotoClick, onEditLog)
        }
    }
}

@Composable
fun ScuteLogCard(
    log: ScuteLogEntity,
    viewModel: ReptileDetailViewModel,
    onPhotoClick: (String) -> Unit,
    onEditLog: (Long) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val photos by produceState<List<ScutePhotoEntity>>(
        initialValue = emptyList(),
        key1 = log.id
    ) {
        viewModel.getPhotosForScuteLog(log.id).collect { value = it }
    }

    val bgColor = when (log.condition) {
        "PIRAMIDING" -> Color(0xFFFFF9C4).copy(alpha = 0.5f) // Yellow100 tint
        "SOFT_SHELL" -> Color(0xFFFFCDD2) // Red100
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when (log.condition) {
        "PIRAMIDING" -> Color(0xFFFFA726) // Orange400
        "SOFT_SHELL" -> Color(0xFFEF5350) // Red400
        else -> MaterialTheme.colorScheme.onSurface
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditLog(log.id) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, if (log.condition == "NORMAL") MaterialTheme.colorScheme.outline else contentColor.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .alpha(0f),
                tint = contentColor
            )
            Column {
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

                LogPhotoRow(photos = photos.map { it.photoUri }, onPhotoClick = onPhotoClick)
            }
        }
    }
}

//@Composable
//fun CareTab(
//    soakingLogs: List<SoakingLogEntity>,
//    uvbLogs: List<UvbBasingLogEntity>,
//    dietLogs: List<DietLogEntity>,
//    onAddSoaking: () -> Unit,
//    onAddUvb: () -> Unit,
//    onAddDiet: () -> Unit
//) {
//    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
//    LazyColumn(
//        modifier = Modifier.fillMaxSize(),
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(24.dp)
//    ) {
//        // Section Soaking
//        item {
//            CareSectionHeader("Soaking (Mandi)", onAddSoaking)
//            if (soakingLogs.isEmpty()) {
//                Text("Belum ada catatan soaking", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
//            }
//        }
//        items(soakingLogs.take(5)) { log ->
//            Card(
//                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//                shape = RoundedCornerShape(14.dp),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
//            ) {
//                Column(modifier = Modifier.padding(12.dp)) {
//                    Text(dateFormatter.format(Date(log.soakingDate)), style = MaterialTheme.typography.labelSmall)
//                    Text("Durasi: ${log.durationMinutes} menit", fontWeight = FontWeight.Bold)
//                    log.waterTempCelsius?.let { Text("Suhu Air: $it°C", style = MaterialTheme.typography.bodySmall) }
//                    if (log.notes.isNotEmpty()) Text(log.notes, style = MaterialTheme.typography.bodySmall)
//                }
//            }
//        }
//
//        // Section UVB
//        item {
//            CareSectionHeader("UVB & Basking", onAddUvb)
//            if (uvbLogs.isEmpty()) {
//                Text("Belum ada catatan UVB", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
//            }
//        }
//        items(uvbLogs.take(5)) { log ->
//            Card(
//                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//                shape = RoundedCornerShape(14.dp),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
//            ) {
//                Column(modifier = Modifier.padding(12.dp)) {
//                    Text(dateFormatter.format(Date(log.sessionDate)), style = MaterialTheme.typography.labelSmall)
//                    Text("${log.uvbType} (${log.durationMinutes} menit)", fontWeight = FontWeight.Bold)
//                    log.basikingTempCelsius?.let { Text("Suhu Basking: $it°C", style = MaterialTheme.typography.bodySmall) }
//                }
//            }
//        }
//
//        // Section Diet
//        item {
//            CareSectionHeader("Diet & Sayuran", onAddDiet)
//            if (dietLogs.isEmpty()) {
//                Text("Belum ada catatan diet", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
//            }
//        }
//        items(dietLogs.take(5)) { log ->
//            Card(
//                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//                shape = RoundedCornerShape(14.dp),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
//            ) {
//                Column(modifier = Modifier.padding(12.dp)) {
//                    Text(dateFormatter.format(Date(log.recordedAt)), style = MaterialTheme.typography.labelSmall)
//                    Text("Sayur: ${log.vegetables}", fontWeight = FontWeight.Bold)
//                    if (log.fruits.isNotEmpty()) Text("Buah: ${log.fruits}", style = MaterialTheme.typography.bodySmall)
//                    if (log.supplements.isNotEmpty()) Text("Suplemen: ${log.supplements}", style = MaterialTheme.typography.bodySmall)
//                }
//            }
//        }
//    }
//}

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
fun BrumasiTab(logs: List<BrumasiLogEntity>, onEditLog: (Long) -> Unit) {
    if (logs.isEmpty()) {
        TabEmptyState(
            icon = Icons.Default.Bedtime,
            title = "Belum ada catatan brumasi",
            subtitle = "Ketuk + untuk menambahkan catatan brumasi pertama"
        )
        return
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = logs,
            key = { it.id }
        ) { log ->
            val isActive = log.endDate == null
            val statusColor = if (isActive) Color(0xFFFFA726) else MaterialTheme.colorScheme.primary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditLog(log.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, if (isActive) statusColor else MaterialTheme.colorScheme.outline)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.TopEnd)
                            .alpha(0f),
                        tint = if (isActive) statusColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isActive) statusColor else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = if (isActive) "Sedang Brumasi" else "Selesai Brumasi",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isActive) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${dateFormatter.format(Date(log.startDate))} — ${log.endDate?.let { dateFormatter.format(Date(it)) } ?: "Berlangsung"}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            log.weightBeforeGrams?.let { Text("Awal: ${it}g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            log.weightAfterGrams?.let { Text("Akhir: ${it}g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                        if (log.notes.isNotEmpty()) Text(log.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun RiwayatTab(
    logs: List<RiwayatEntity>,
    viewModel: ReptileDetailViewModel,
    onPhotoClick: (String) -> Unit,
    onEditRiwayat: (Long) -> Unit
) {
    if (logs.isEmpty()) {
        TabEmptyState(
            icon = Icons.Default.HealthAndSafety,
            title = "Belum ada riwayat sakit",
            subtitle = "Ketuk + untuk menambahkan riwayat"
        )
        return
    }

    remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = logs,
            key = { it.id }
        ) { log ->
            RiwayatCard(log, viewModel, onPhotoClick, onEditRiwayat)
        }
    }
}

@Composable
fun RiwayatCard(
    riwayat: RiwayatEntity,
    viewModel: ReptileDetailViewModel,
    onPhotoClick: (String) -> Unit,
    onEditRiwayat: (Long) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val photos by produceState<List<RiwayatPhotoEntity>>(
        initialValue = emptyList(),
        key1 = riwayat.id
    ) {
        viewModel.getPhotosForRiwayat(riwayat.id).collect { value = it }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditRiwayat(riwayat.id) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (riwayat.isOngoing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline)
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (riwayat.isOngoing) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                text = "Sedang Sakit",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
                Text(
                    text = dateFormatter.format(Date(riwayat.startDate)),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                LogPhotoRow(photos = photos.map { it.photoUri }, onPhotoClick = onPhotoClick)
            }
        }
    }
}

@Composable
fun TabEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
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
