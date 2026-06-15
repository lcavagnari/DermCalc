package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.ui.component.input.ButtonsTray
import it.lcavagnari.pdm.dermcalc.ui.portrait.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding.OnboardingPager
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.launch

/**
 * Represents a single page in the onboarding flow.
 */
data class OnboardingScreen(
    @param:StringRes val title: Int,
    @param:StringRes val description: Int? = null,
    @param:DrawableRes val backgroundLight: Int,
    @param:DrawableRes val backgroundDark: Int,
    val imageRes: ImageVector? = null,
    val imageDrawable: Int? = null,
    val imageSize: Dp = 280.dp,
    val inputFieldIds: List<String> = emptyList(),
)

/** Anatomical districts for Onboarding calculator. */
val onboardingScreens = listOf(
    OnboardingScreen(
        title = R.string.onboarding_1_title,
        description = null,
        imageDrawable = R.drawable.ic_dermatology,
        backgroundLight = R.drawable.bg_onboarding1,
        backgroundDark = R.drawable.bg_onboarding1_dark
    ),
    OnboardingScreen(
        title = R.string.onboarding_2_title,
        description = R.string.onboarding_2_desc,
        imageRes = Icons.Default.Info,
        backgroundLight = R.drawable.bg_onboarding2,
        backgroundDark = R.drawable.bg_onboarding2_dark
    ),
    OnboardingScreen(
        title = R.string.onboarding_3_title,
        description = R.string.onboarding_3_desc,
        imageRes = Icons.Default.Favorite,
        backgroundLight = R.drawable.bg_onboarding3,
        backgroundDark = R.drawable.bg_onboarding3_dark
    ),
    OnboardingScreen(
        title = R.string.onboarding_4_title,
        description = R.string.onboarding_4_desc,
        inputFieldIds = listOf("full-name", "date-of-birth", "sex"),
        imageRes = Icons.Default.AccountCircle,
        imageSize = 190.dp,
        backgroundLight = R.drawable.bg_onboarding4,
        backgroundDark = R.drawable.bg_onboarding3_dark
    ),
    OnboardingScreen(
        title = R.string.onboarding_5_title,
        description = R.string.onboarding_5_desc,
        inputFieldIds = listOf("height", "weight"),
        imageRes = Icons.Default.AccountCircle,
        imageSize = 180.dp,
        backgroundLight = R.drawable.bg_onboarding4,
        backgroundDark = R.drawable.bg_onboarding3_dark
    )
)

private val vm:(OnboardingModel) -> Unit = {
    it.updateName("Asriel ")
    it.updateDateOfBirth(today().date)
    it.updateHeightMetric(172)
    it.updateWeightKilos(67)
}

// Light
@Preview(showBackground = true) @Composable private fun OnboardingRegularPreview() {
    DermCalcPreview(setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 0)
        )
    }
}
@Preview(showBackground = true) @Composable private fun OnboardingDarkRegularPreview() {
    DermCalcPreview(darkTheme = true, setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 0)
        )
    }
}

@Preview(showBackground = true) @Composable private fun OnboardingRegularPreview1() {
    DermCalcPreview(setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 1)
        )
    }
}
@Preview(showBackground = true) @Composable private fun OnboardingDarkRegularPreview1() {
    DermCalcPreview(darkTheme = true, setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 1)
        )
    }
}

@Preview(showBackground = true) @Composable private fun OnboardingRegularPreview2() {
    DermCalcPreview(setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 2)
        )
    }
}
@Preview(showBackground = true) @Composable private fun OnboardingDarkRegularPreview2() {
    DermCalcPreview(darkTheme = true, setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 2)
        )
    }
}

@Preview(showBackground = true) @Composable private fun OnboardingRegularPreview3() {
    DermCalcPreview(setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 3)
        )
    }
}
@Preview(showBackground = true) @Composable private fun OnboardingDarkRegularPreview3() {
    DermCalcPreview(darkTheme = true, setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 3)
        )
    }
}

@Preview(showBackground = true) @Composable private fun OnboardingRegularPreview4() {
    DermCalcPreview(setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 4)
        )
    }
}
@Preview(showBackground = true) @Composable private fun OnboardingDarkRegularPreview4() {
    DermCalcPreview(darkTheme = true, setupOm = vm) { vm, _, _, _ ->
        OnboardingScreen(onboardingModel = vm, onFinish = {},
            pagerState = rememberPagerState(pageCount = { onboardingScreens.size }, initialPage = 4)
        )
    }
}



/**
 * Full-screen onboarding flow over a multipage [androidx.compose.foundation.pager.HorizontalPager].
 */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(pageCount = { onboardingScreens.size }),
    onboardingModel: OnboardingModel,
    onToggleTheme: () -> Unit = {},
    onLangClick: () -> Unit = {},
    onFinish: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val fields by onboardingModel.fields.collectAsState()

    val currentScreen = onboardingScreens[pagerState.currentPage]
    val isLastIndex = pagerState.currentPage == onboardingScreens.lastIndex
    val isBtnEnabled = onboardingModel.isFieldsInputValid(currentScreen.inputFieldIds, fields)

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        OnboardingPager(
            modifier = Modifier.fillMaxSize(),
            pagerState = pagerState,
            onboardingModel = onboardingModel,
            userScrollEnabled = isBtnEnabled
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                ButtonsTray(
                    iconTint = MaterialTheme.colorScheme.secondary,
                    onLangClick = onLangClick,
                    onToggleTheme = onToggleTheme
                )
            }

            Spacer(Modifier.weight(1f))

            if (!isBtnEnabled)
                GoBackButton(modifier = Modifier.padding(bottom = 7.dp, start = 5.dp)) {
                    if (pagerState.currentPage > 0)
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }

            StepIndicator(
                totalSteps = onboardingScreens.size,
                currentStep = pagerState.currentPage
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .semantics { testTag = if (isLastIndex) "btn_start" else "btn_next" },
                enabled = isBtnEnabled,
                shape = RoundedCornerShape(5.dp),
                onClick = {
                    if (!isLastIndex) {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else onFinish()
                }
            ) { Text(stringResource(if (isLastIndex) R.string.btn_start else R.string.btn_next)) }
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Back arrow with label.
 */
@Composable
private fun GoBackButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val dark = LocalDarkTheme.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = if (dark) 0f else 0.6f))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Row of dots indicating the current page position.
 */
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
