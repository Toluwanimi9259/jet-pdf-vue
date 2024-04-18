package com.techafresh.jetpdf

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.pratikk.jetpdfvue.state.HorizontalVueReaderState
import com.pratikk.jetpdfvue.state.VueFilePicker
import com.pratikk.jetpdfvue.state.VueImportSources
import com.pratikk.jetpdfvue.state.VueLoadState
import com.pratikk.jetpdfvue.state.VueResourceType
import com.pratikk.jetpdfvue.state.rememberHorizontalVueReaderState
import com.pratikk.jetpdfvue.util.compressImageToThreshold
import kotlinx.coroutines.launch



@Composable
fun HorizontalPdfViewer(horizontalVueReaderState: HorizontalVueReaderState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = horizontalVueReaderState.getImportLauncher(interceptResult = {
        it.compressImageToThreshold(2)
    })

    BoxWithConstraints(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        val configuration = LocalConfiguration.current
        val containerSize = remember {
            IntSize(constraints.maxWidth, constraints.maxHeight)
        }

        LaunchedEffect(Unit) {
            horizontalVueReaderState.load(
                context = context,
                coroutineScope = scope,
                containerSize = containerSize,
                isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT,
                customResource = null
            )
        }
        val vueLoadState = horizontalVueReaderState.vueLoadState
        when(vueLoadState){
            is VueLoadState.DocumentError -> {
                /**
                 * Handle Error by using
                 * vueLoadState.getErrorMessage
                 * */
            }
            VueLoadState.DocumentImporting -> {
                /**
                 * Indicates when image/pdf is being imported
                 * This is also the state when the image is done importing but is being processed
                 * */
            }
            VueLoadState.DocumentLoaded -> {
                /**
                 * This is the state where either
                 * HorizontalPdfViewer(horizontalVueReaderState = horizontalVueReaderState)
                 * or
                 * VerticalPdfViewer(verticalVueReaderState = verticalVueReaderState)
                 * Is used to display pdf
                 * */
            }
            VueLoadState.DocumentLoading -> {
                /**
                 * Indicates when image/pdf is loaded initially
                 * This is also the state when resource type is custom
                 * */
            }
            VueLoadState.NoDocument -> {
                /**
                 * This is the state where you want to create a new document
                 * Here, show UI for ex, button to launch the import intent
                 * */
            }
        }
        when (vueLoadState) {
            is VueLoadState.NoDocument -> {
                Button(onClick = {
                    horizontalVueReaderState.file?.delete() // delete the previous file before adding another one
                    horizontalVueReaderState.launchImportIntent(
                        context = context,
                        launcher = launcher
                    )
                }) {
                    Text(text = "Import Document")
                }
            }

            is VueLoadState.DocumentError -> {
                Column {
                    Text(text = "Error:  ${horizontalVueReaderState.vueLoadState.getErrorMessage}")
                    Button(onClick = {
                        scope.launch {
                            horizontalVueReaderState.load(
                                context = context,
                                coroutineScope = scope,
                                containerSize = containerSize,
                                isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT,
                                customResource = null
                            )
                        }
                    }) {
                        Text(text = "Retry")
                    }
                }
            }

            is VueLoadState.DocumentImporting -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(text = "Importing...")
                }
            }

            is VueLoadState.DocumentLoaded -> {
                HorizontalSampleA(horizontalVueReaderState = horizontalVueReaderState) {
                    horizontalVueReaderState.file?.delete() // delete the previous file before adding another one
                    horizontalVueReaderState.launchImportIntent(
                        context = context,
                        launcher = launcher
                    )
                }
            }

            is VueLoadState.DocumentLoading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(text = "Loading ${horizontalVueReaderState.loadPercent}")
                }
            }
        }
    }
}