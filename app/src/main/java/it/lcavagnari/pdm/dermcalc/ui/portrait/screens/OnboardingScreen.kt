package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.ui.shared.component.TopTrayButtons
import it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding.OnboardingPager
import kotlinx.coroutines.launch

/**
 * Represents a single page in the onboarding flow.
 *
 * @property title - text displayed as the page heading.
 * @property description - optional subtitle shown below the title.
 * @property imageRes - optional vector icon displayed on the page.
 * @property imageDrawable - optional drawable resource id for the page image.
 * @property imageSize - size applied to the image composable. Defaults to 280.dp.
 * @property inputFieldIds - ids of [InputField] instances rendered on this page.
 * @property inputFieldId - deprecated. use [inputFieldIds] instead.
 * @constructor Create empty Onboarding screen
 */
data class OnboardingScreen(
    @StringRes val title: Int,
    @StringRes val description: Int? = null,

    val imageRes: ImageVector? = null,
    val imageDrawable: Int? = null,
    val imageSize: Dp? = 280.dp,

    val inputFieldIds: List<String> = emptyList(),

    @Deprecated("Use inputFieldIds instead", ReplaceWith("inputFieldIds"), DeprecationLevel.ERROR)
    val inputFieldId: String? = null
)

val onboardingScreens = listOf(
    OnboardingScreen(
        title = R.string.onboarding_1_title,
        imageDrawable = R.drawable.ic_ecg
    ),
    OnboardingScreen(
        title = R.string.onboarding_2_title,
        description = R.string.onboarding_2_desc,
        imageRes = Icons.Default.Info
    ),
    OnboardingScreen(
        title = R.string.onboarding_3_title,
        description = R.string.onboarding_3_desc,
        imageRes = Icons.Default.Favorite
    ),
    OnboardingScreen(
        title = R.string.onboarding_4_title,
        description = R.string.onboarding_4_desc,
        inputFieldIds = listOf("full-name", "date-of-birth", "sex"),
        imageRes = Icons.Default.AccountCircle,
        imageSize = 190.dp
    ),
    OnboardingScreen(
        title = R.string.onboarding_5_title,
        description = R.string.onboarding_5_desc,
        inputFieldIds = listOf("height", "weight"),
        imageRes = Icons.Default.AccountCircle,
        imageSize = 180.dp
    )
)


@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    OnboardingScreen(
        rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 4),
        modifier = Modifier.fillMaxSize(),
        onFinish = {})
}

@Composable
fun OnboardingScreen(
    pagerState: PagerState,
    modifier: Modifier,
    onToggleTheme: () -> Unit = {},
    onLangClick: () -> Unit = {},
    onFinish: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val onBoardingModel: OnboardingModel = viewModel()
    val focusManager = LocalFocusManager.current
    val fields by onBoardingModel.fields.collectAsState()

    val currentScreen = onboardingScreens[pagerState.currentPage]
    val isLastIndex = pagerState.currentPage == onboardingScreens.lastIndex
    val isBtnEnabled = onBoardingModel.isFieldsInputValid(currentScreen.inputFieldIds, fields)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            // Tapping anywhere outside a text field clears focus and dismisses the keyboard.
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TopTrayButtons(
                showDebug = true,
                iconTint = MaterialTheme.colorScheme.secondary,
                onLangClick = onLangClick,
                onDebugClick = { onBoardingModel.finishOnboarding() },
                onToggleTheme = onToggleTheme
            )
        }

        // Contenuto pagina
        OnboardingPager(
            pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 30.dp),
            userScrollEnabled = isBtnEnabled
        )

        // Bottone "indietro"
        if (!isBtnEnabled)
            GoBackButton(modifier = Modifier.padding(bottom = 7.dp, start = 5.dp)) {
                if (pagerState.currentPage > 0)
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            }

        // Indicatore pagina
        StepIndicator(
            totalSteps = onboardingScreens.size,
            currentStep = pagerState.currentPage
        )

        // Bottone "Avanti"
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics { testTag = if (isLastIndex) "btn_start" else "btn_next" },
            enabled = isBtnEnabled,
            onClick = {
                if (!isLastIndex) {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinish()
                }
            }
        ) {
            Text(stringResource(if (isLastIndex) R.string.btn_start else R.string.btn_next))
        }
    }

    if (isBtnEnabled) {
        if (pagerState.currentPage > 0) Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(26.dp),
                painter = painterResource(id = R.drawable.ic_swipe_left),
                contentDescription = "Swipe back",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
            )
        }

        if (!isLastIndex) Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(26.dp),
                painter = painterResource(id = R.drawable.ic_swipe_right),
                contentDescription = "Swipe forward",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
            )
        }
    }
}


@Composable
private fun GoBackButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .wrapContentSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back button",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = stringResource(R.string.btn_back),
            modifier = Modifier.padding(start = 25.dp),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
private fun StepIndicator(
    modifier: Modifier = Modifier,
    totalSteps: Int, currentStep: Int
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            // Active dot stretches to 20dp; inactive dots stay at 8dp (pill vs circle).
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (isActive) 20.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    .animateContentSize(),
            )
        }
    }
}