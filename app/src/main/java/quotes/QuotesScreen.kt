package edu.metrostate.ics342.mediatracker.ui.quotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.metrostate.ics342.mediatracker.data.model.Quote

@Composable
fun QuotesScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuotesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        IconButton(
            onClick = onNavigateBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Text(
            text = "Quotes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        when (val state = uiState) {

            QuotesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is QuotesUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Button(
                        onClick = viewModel::retry
                    ) {
                        Text("Retry")
                    }
                }
            }

            is QuotesUiState.Success -> {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected =
                            state.listType ==
                                    QuotesListType.MY_QUOTES,
                        onClick = viewModel::loadMyQuotes,
                        label = {
                            Text("My Quotes")
                        }
                    )

                    FilterChip(
                        selected =
                            state.listType ==
                                    QuotesListType.PUBLIC_QUOTES,
                        onClick = viewModel::loadPublicQuotes,
                        label = {
                            Text("Public Quotes")
                        }
                    )
                }

                state.message?.let { message ->
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = message,
                        color =
                            if (
                                message.contains(
                                    "updated",
                                    ignoreCase = true
                                ) ||
                                message.contains(
                                    "deleted",
                                    ignoreCase = true
                                )
                            ) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                if (state.quotes.isEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text =
                                if (
                                    state.listType ==
                                    QuotesListType.MY_QUOTES
                                ) {
                                    "No quotes saved yet — add one from a book's detail page."
                                } else {
                                    "No public quotes available yet."
                                }
                        )
                    }

                } else {

                    LazyColumn(
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.quotes,
                            key = { quote ->
                                quote.id
                            }
                        ) { quote ->

                            QuoteCard(
                                quote = quote,
                                listType = state.listType,
                                isLiked =
                                    quote.id in
                                            state.likedQuoteIds,
                                isBusy =
                                    quote.id in
                                            state.busyQuoteIds,
                                onEdit = { updatedText,
                                           updatedPage,
                                           updatedPublic ->

                                    viewModel.updateQuote(
                                        quoteId = quote.id,
                                        quoteText = updatedText,
                                        pageNumberText = updatedPage,
                                        isPublic = updatedPublic
                                    )
                                },
                                onDelete = {
                                    viewModel.deleteQuote(
                                        quote.id
                                    )
                                },
                                onToggleLike = {
                                    viewModel.toggleLike(
                                        quote.id
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    listType: QuotesListType,
    isLiked: Boolean,
    isBusy: Boolean,
    onEdit: (
        quoteText: String,
        pageNumber: String,
        isPublic: Boolean
    ) -> Unit,
    onDelete: () -> Unit,
    onToggleLike: () -> Unit
) {
    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "\"${quote.quoteText}\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic
            )

            quote.pageNumber?.let { page ->
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Page $page",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = quote.media.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    if (quote.isPublic) {
                        "Public"
                    } else {
                        "Private"
                    },
                style = MaterialTheme.typography.labelMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "${quote.likeCount} likes",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (
                listType ==
                QuotesListType.MY_QUOTES
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showEditDialog = true
                        },
                        enabled = !isBusy,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }

                    Button(
                        onClick = {
                            showDeleteDialog = true
                        },
                        enabled = !isBusy,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
                    }
                }
            } else {
                Button(
                    onClick = onToggleLike,
                    enabled = !isBusy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isLiked) {
                            "Unlike"
                        } else {
                            "Like"
                        }
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        EditQuoteDialog(
            quote = quote,
            onDismiss = {
                showEditDialog = false
            },
            onSave = {
                    text,
                    pageNumber,
                    isPublic ->

                onEdit(
                    text,
                    pageNumber,
                    isPublic
                )

                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete Quote")
            },
            text = {
                Text(
                    "Are you sure you want to delete this quote?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EditQuoteDialog(
    quote: Quote,
    onDismiss: () -> Unit,
    onSave: (
        quoteText: String,
        pageNumber: String,
        isPublic: Boolean
    ) -> Unit
) {
    var quoteText by remember {
        mutableStateOf(quote.quoteText)
    }

    var pageNumber by remember {
        mutableStateOf(
            quote.pageNumber?.toString() ?: ""
        )
    }

    var isPublic by remember {
        mutableStateOf(quote.isPublic)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Quote")
        },
        text = {
            Column {

                OutlinedTextField(
                    value = quoteText,
                    onValueChange = { value ->
                        if (value.length <= 500) {
                            quoteText = value
                        }
                    },
                    label = {
                        Text("Quote")
                    },
                    supportingText = {
                        Text("${quoteText.length}/500")
                    },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = pageNumber,
                    onValueChange = { value ->
                        if (
                            value.isBlank() ||
                            value.all { character ->
                                character.isDigit()
                            }
                        ) {
                            pageNumber = value
                        }
                    },
                    label = {
                        Text("Page number")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text("Public Quote")

                    Switch(
                        checked = isPublic,
                        onCheckedChange = {
                            isPublic = it
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        quoteText,
                        pageNumber,
                        isPublic
                    )
                },
                enabled = quoteText.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}