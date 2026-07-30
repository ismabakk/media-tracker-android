package edu.metrostate.ics342.mediatracker.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.MediaDetail

@Composable
fun MediaDetailScreen(
    mediaId: Int,
    onNavigateBack: () -> Unit,
    onWriteReview: (Int) -> Unit,
    viewModel: MediaDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mediaId) {
        viewModel.load(mediaId)
    }

    when (val state = uiState) {
        MediaDetailUiState.Loading -> {
            LoadingScreen()
        }

        is MediaDetailUiState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry = viewModel::retry,
                onNavigateBack = onNavigateBack
            )
        }

        is MediaDetailUiState.Success -> {
            MediaDetailContent(
                media = state.detail,
                libraryStatus = state.libraryStatus,
                onNavigateBack = onNavigateBack,
                onWriteReview = onWriteReview
            )
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(16.dp))

            Button(onClick = onRetry) {
                Text("Retry")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onNavigateBack) {
                Text("Go Back")
            }
        }
    }
}

@Composable
private fun MediaDetailContent(
    media: MediaDetail,
    libraryStatus: LibraryStatus?,
    onNavigateBack: () -> Unit,
    onWriteReview: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Surface(
            modifier = Modifier.size(
                width = 120.dp,
                height = 160.dp
            ),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (media.mediaType) {
                        "book" -> Icons.Outlined.MenuBook
                        "movie" -> Icons.Outlined.Movie
                        else -> Icons.Outlined.Tv
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = media.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = media.creatorName(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (media.ratingCount > 0) {
                "★ ${media.averageRating} (${media.ratingCount})"
            } else {
                "Not yet rated"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoBox(
                label = "Year",
                value = media.publishedYear?.toString() ?: "Unknown",
                modifier = Modifier.weight(1f)
            )

            InfoBox(
                label = media.middleStatLabel(),
                value = media.middleStatValue(),
                modifier = Modifier.weight(1f)
            )

            InfoBox(
                label = "Genre",
                value = media.genres.firstOrNull() ?: "Unknown",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "About",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = media.description ?: "No description is available.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                // Adding to the library will be connected next.
            },
            enabled = libraryStatus == null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = when (libraryStatus) {
                    LibraryStatus.WANT_TO -> "In Library: Want To"
                    LibraryStatus.IN_PROGRESS -> "In Library: In Progress"
                    LibraryStatus.FINISHED -> "In Library: Finished"
                    null -> "+ Want To"
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onWriteReview(media.id) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Write Review")
        }
    }
}

@Composable
private fun InfoBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun MediaDetail.creatorName(): String {
    return author ?: director ?: creator ?: "Unknown"
}

private fun MediaDetail.middleStatLabel(): String {
    return when (mediaType) {
        "book" -> "Pages"
        "movie" -> "Runtime"
        "show" -> "Seasons"
        else -> "Type"
    }
}

private fun MediaDetail.middleStatValue(): String {
    return when (mediaType) {
        "book" -> pageCount?.toString() ?: "Unknown"
        "movie" -> runtimeMinutes?.let { "$it min" } ?: "Unknown"
        "show" -> seasonCount?.toString() ?: "Unknown"
        else -> mediaType.replaceFirstChar { it.uppercase() }
    }
}