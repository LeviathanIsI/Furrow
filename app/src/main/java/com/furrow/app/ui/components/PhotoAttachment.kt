package com.furrow.app.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.furrow.app.ui.theme.AppRadius
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.BorderSubtle
import com.furrow.app.ui.theme.BorderVisible
import com.furrow.app.ui.theme.Obsidian
import com.furrow.app.ui.theme.TextPrimary
import com.furrow.app.ui.theme.TextTertiary
import com.furrow.app.util.PhotoHelper
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAttachment(
    photoUri: String?,
    onPhotoChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showSourceSheet by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = PhotoHelper.persistPhoto(context, it)
            onPhotoChanged(path)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let { uri ->
                val path = PhotoHelper.persistPhoto(context, uri)
                onPhotoChanged(path)
            }
        }
    }

    val shape = RoundedCornerShape(AppRadius.input)

    if (photoUri != null) {
        val bitmap = remember(photoUri) {
            val file = File(photoUri)
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap() else null
        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape),
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Attached photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(shape),
                )
            }
            IconButton(
                onClick = {
                    PhotoHelper.deletePhoto(photoUri)
                    onPhotoChanged(null)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppSpacing.xs)
                    .size(32.dp)
                    .background(Obsidian.copy(alpha = 0.7f), RoundedCornerShape(AppRadius.sm)),
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove photo",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(
                    width = 1.dp,
                    color = BorderSubtle,
                    shape = shape,
                )
                .clip(shape)
                .clickable { showSourceSheet = true },
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    "Add Photo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                )
            }
        }
    }

    if (showSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSourceSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Obsidian,
            shape = RoundedCornerShape(topStart = AppRadius.sheet, topEnd = AppRadius.sheet),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = AppSpacing.xs, bottom = AppSpacing.xs)
                        .size(width = 40.dp, height = 4.dp)
                        .background(BorderVisible, RoundedCornerShape(AppRadius.sm)),
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppSpacing.lg),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showSourceSheet = false
                            val uri = PhotoHelper.createTempPhotoUri(context)
                            cameraUri = uri
                            cameraLauncher.launch(uri)
                        }
                        .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = TextPrimary)
                    Text("Take Photo", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showSourceSheet = false
                            galleryLauncher.launch("image/*")
                        }
                        .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = TextPrimary)
                    Text("Choose from Gallery", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
            }
        }
    }
}
