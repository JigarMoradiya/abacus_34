package com.jigar.me.ui.view.other.review_submit

import android.app.Activity
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.github.dhaval2404.imagepicker.ImagePicker
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.other.review_submit.viewmodels.ReviewAdminStatus
import com.jigar.me.ui.view.other.review_submit.viewmodels.ReviewSubmitViewModel
import com.jigar.me.utils.extensions.toastL
import com.jigar.me.utils.extensions.toastS

@Composable
fun ReviewSubmitScreen(
    onFinish: () -> Unit,
) {
    val viewModel: ReviewSubmitViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri -> viewModel.onImagePicked(uri) }
        }
    }

    fun launchImagePicker() {
        val activity = context as? Activity ?: return
        ImagePicker.with(activity)
            .compress(700)
            .galleryOnly()
            .maxResultSize(1080, 1080)
            .createIntent { intent -> imagePickerLauncher.launch(intent) }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar — back button + logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimens.Dimens8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.Dimens45)
                )
            }

            // Scrollable form
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(AppDimens.Dimens20),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.review_submit),
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(AppDimens.Dimens6))
                Text(
                    text = stringResource(R.string.review_fill_detail),
                    fontSize = 13.sp,
                    color = Color.Black,
                )

                if (uiState.adminStatus != ReviewAdminStatus.NONE) {
                    AdminStatusBanner(
                        status = uiState.adminStatus,
                        message = uiState.adminMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppDimens.Dimens16)
                    )
                }

                Spacer(modifier = Modifier.height(AppDimens.Dimens16))

                LevelDropdown(
                    options = uiState.levelOptions,
                    selectedIndex = uiState.selectedLevelIndex,
                    enabled = !uiState.isFormReadOnly,
                    onSelected = viewModel::onLevelSelected,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AppDimens.Dimens10))

                if (!uiState.isFormReadOnly) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (uiState.adminStatus == ReviewAdminStatus.REJECTED)
                                stringResource(R.string.change_screenshot_of_application_review)
                            else
                                stringResource(R.string.add_screenshot_of_application_review),
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(AppDimens.Dimens4))
                                .background(colorResource(id = R.color.gray_e6))
                                .border(
                                    1.dp,
                                    colorResource(id = R.color.gray_c7),
                                    RoundedCornerShape(AppDimens.Dimens4)
                                )
                                .clickable { launchImagePicker() }
                                .padding(
                                    horizontal = AppDimens.Dimens8,
                                    vertical = AppDimens.Dimens4
                                )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                ReviewImagePreview(
                    pickedImagePath = uiState.pickedImagePath,
                    existingImageUrl = uiState.existingImageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppDimens.Dimens10)
                )

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    placeholder = { Text(stringResource(R.string.description)) },
                    enabled = !uiState.isFormReadOnly,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppDimens.Dimens8)
                        .height(AppDimens.Dimens150)
                )

                if (!uiState.isFormReadOnly) {
                    Spacer(modifier = Modifier.height(AppDimens.Dimens20))
                    Button(
                        onClick = { viewModel.onSubmit() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.colorPrimaryDark),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(AppDimens.Dimens24),
                    ) {
                        Text(
                            text = if (uiState.adminStatus == ReviewAdminStatus.REJECTED)
                                stringResource(R.string.submit_new_request)
                            else
                                stringResource(R.string.submit),
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppDimens.Dimens12))
            }

            if (!uiState.isFormReadOnly) {
                Text(
                    text = stringResource(R.string.review_note),
                    color = Color.Black,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens10)
                )
            }
        }
    }

    if (uiState.isLoading) {
        Loader()
    }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            viewModel.consumeError()
        }
    }

    uiState.toastMessageRes?.let { resId ->
        LaunchedEffect(resId) {
            context.toastS(context.getString(resId))
            viewModel.consumeToast()
        }
    }

    uiState.finishActivity?.consume { onFinish() }
}

@Composable
private fun AdminStatusBanner(
    status: ReviewAdminStatus,
    message: String?,
    modifier: Modifier = Modifier,
) {
    val (bg, statusText, statusColor) = when (status) {
        ReviewAdminStatus.REJECTED -> Triple(
            colorResource(id = R.color.red_50),
            stringResource(R.string.sorry_your_submitted_review_is_rejected_please_try_again),
            colorResource(id = R.color.red_900),
        )
        ReviewAdminStatus.APPROVED -> Triple(
            colorResource(id = R.color.green_50),
            stringResource(R.string.congratulations_your_submitted_review_is_approved),
            colorResource(id = R.color.green_900),
        )
        ReviewAdminStatus.PENDING -> Triple(
            colorResource(id = R.color.blue_grey_50),
            stringResource(R.string.your_request_successfully_submitted),
            Color.Black,
        )
        else -> return
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(bg)
            .padding(AppDimens.Dimens12)
    ) {
        Text(text = statusText, color = statusColor, fontSize = 13.sp)
        val secondary = if (status == ReviewAdminStatus.PENDING) {
            stringResource(R.string.please_wait_white_we_are_reviewing_your_request)
        } else {
            message
        }
        if (status != ReviewAdminStatus.APPROVED && !secondary.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(AppDimens.Dimens2))
            Text(text = secondary, color = Color.Black, fontSize = 13.sp)
        }
    }
}

@Composable
private fun LevelDropdown(
    options: List<String>,
    selectedIndex: Int,
    enabled: Boolean,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val label = options.getOrNull(selectedIndex)
        ?: stringResource(R.string.select_level)

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.Dimens36)
                .clip(RoundedCornerShape(AppDimens.Dimens4))
                .border(
                    1.dp,
                    colorResource(id = R.color.gray_c7),
                    RoundedCornerShape(AppDimens.Dimens4)
                )
                .clickable(enabled = enabled) { expanded = true }
                .padding(start = AppDimens.Dimens12, end = AppDimens.Dimens6),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = Color.Black
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ReviewImagePreview(
    pickedImagePath: String?,
    existingImageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val src: Any? = pickedImagePath ?: existingImageUrl
    if (src != null) {
        AsyncImage(
            model = src,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.placeholder),
            contentScale = ContentScale.Fit,
            modifier = modifier.height(120.dp)
        )
    }
}
