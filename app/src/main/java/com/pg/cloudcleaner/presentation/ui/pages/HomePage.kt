package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.presentation.WorkerUIState
import com.pg.cloudcleaner.presentation.vm.HomeVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComposable(viewModel: HomeVM = viewModel()) {
    val scanStatus by viewModel.scanUIStatus.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = R.drawable.logo,
                            contentDescription = "App Logo",
                            modifier = Modifier.size(48.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(text = "FreeMemory")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when (scanStatus) {
                // ── Not yet scanned ───────────────────────────────────────
                is WorkerUIState.Initial -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AsyncImage(
                            model = R.drawable.logo,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Free Up Your Space",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Scan your device to find duplicate files, large files, and media you can safely remove.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = { viewModel.restartScan() }) {
                            Text("Start Scan")
                        }
                    }
                }

                // ── Scanning in progress ──────────────────────────────────
                is WorkerUIState.InProgress -> {
                    val inProgressState = scanStatus as WorkerUIState.InProgress
                    ScanningComposable(
                        progress = inProgressState.progress,
                        message = inProgressState.message,
                        vm = viewModel,
                    )
                }

                // ── Scan finished ─────────────────────────────────────────
                is WorkerUIState.Success -> {
                    ScanResultComposable(vm = viewModel)
                }

                // ── Error states ──────────────────────────────────────────
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Something went wrong",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "We couldn't load your storage data. Please try scanning again.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { viewModel.restartScan() }) {
                            Text("Scan Again")
                        }
                    }
                }
            }
        }
    }
}
