package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding.OnboardingPager
import kotlinx.coroutines.launch

data class OnboardingScreen(
    val title: String,
    val description: String? = null,

    val imageRes: ImageVector? = null,
    val imageDrawable: Int? = null,
    val imageSize: Dp? = 280.dp,

    val inputFieldIds: List<String> = emptyList(),

    @Deprecated("Use inputFieldIds instead", ReplaceWith("inputFieldIds"), DeprecationLevel.ERROR)
    val inputFieldId: String? = null
)

val onboardingScreens = listOf(
    OnboardingScreen(
        title = "Welcome to DermCalc",
        imageDrawable = R.drawable.ic_ecg
    ),
    OnboardingScreen(
        title = "Numbers that matter",
        description = "BMI, BSA, PASI — sounds intimidating, but we'll walk you through it.",
        imageRes = Icons.Default.Info
    ),
    OnboardingScreen(
        title = "Always with you",
        description = "Your history, your scores, your progress — right in your pocket.",
        imageRes = Icons.Default.Favorite

    ),
    OnboardingScreen(
        title = "Let's get to know you!",
        description = "A little info goes a long way toward making this truly yours.",
        inputFieldIds = listOf("full-name", "date-of-birth", "sex"),
        imageRes = Icons.Default.AccountCircle,
        imageSize = 190.dp
    ),
    OnboardingScreen(
        title = "We require a little more information about you.",
        description = "Please provide all the required information to personalize your experience.",
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
fun OnboardingScreen(pagerState: PagerState, modifier: Modifier, onFinish: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val onBoardingModel: OnboardingModel = viewModel()
    val focusManager = LocalFocusManager.current
    val fields by onBoardingModel.fields.collectAsState()

    val currentScreen = onboardingScreens[pagerState.currentPage]
    val isLastIndex = pagerState.currentPage == onboardingScreens.lastIndex
    val isBtnEnabled = onBoardingModel.isPageInputValid(currentScreen.inputFieldIds, fields)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            // Tapping anywhere outside a text field clears focus and dismisses the keyboard.
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
    ) {

        // Contenuto pagina
        OnboardingPager(
            pagerState,
            modifier = Modifier.weight(1f).padding(bottom = 30.dp),
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
                .padding(16.dp),
            enabled = isBtnEnabled,
            onClick = {
                if (!isLastIndex) {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinish()
                }
            }
        ) {
            Text(if (isLastIndex) "Start" else "Next")
        }
    }

    if (isBtnEnabled) {
        if (pagerState.currentPage > 0) Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.padding(start = 12.dp).size(32.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                modifier = Modifier.padding(end = 12.dp).size(32.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Swipe forward",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
            )
        }
    }
}


@Composable
private fun GoBackButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.clickable(onClick = onClick)
                    .fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        Icon(
            modifier = modifier.size(20.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back button",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text("Go Back ...",
            modifier = modifier.padding(start = 25.dp),
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