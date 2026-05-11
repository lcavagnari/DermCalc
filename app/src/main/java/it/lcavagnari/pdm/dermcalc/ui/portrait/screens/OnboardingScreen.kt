package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding.OnboardingPager
import kotlinx.coroutines.launch

data class OnboardingScreen(
    val title: String,
    val description: String,
    val imageRes: ImageVector? = null,
    val inputFieldIds: List<String> = emptyList(),

    @Deprecated("Use inputFieldIds instead", ReplaceWith("inputFieldIds"), DeprecationLevel.ERROR)
    val inputFieldId: String? = null
)

val onboardingScreens = listOf(
    OnboardingScreen(
        title = "Groceries at Your Fingertips",
        description = "Discover nearby grocery stores and essentials in minutes.",
        imageRes = Icons.Default.ShoppingCart
    ),
    OnboardingScreen(
        title = "Fresh Delivered, Hassle Free",
        description = "Get fresh picks delivered fast, right when you need them.",
        imageRes = Icons.Default.MailOutline
    ),
    OnboardingScreen(
        title = "Shop Smart. Eat Fresh",
        description = "Save time with smart ordering and healthy daily choices.",
        imageRes = Icons.Default.CheckCircle
    ),

    OnboardingScreen(
        title = "Tell us about yourself!",
        description = "Please provide all the required information to personalize your experience.",
        inputFieldIds = listOf("full-name", "date-of-birth", "sex")
    ),
    OnboardingScreen(
        title = "We require a little more information about you.",
        description = "Please provide all the required information to personalize your experience.",
        inputFieldIds = listOf("height", "weight"),
    )
)


@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    OnboardingScreen(
        rememberPagerState(pageCount = { onboardingScreens.size }),
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
        OnboardingPager(
            pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = isBtnEnabled
        )

        StepIndicator(
            totalSteps = onboardingScreens.size,
            currentStep = pagerState.currentPage
        )

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
}


@Composable
private fun StepIndicator(
    totalSteps: Int, currentStep: Int,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Row(
        modifier = modifier,
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