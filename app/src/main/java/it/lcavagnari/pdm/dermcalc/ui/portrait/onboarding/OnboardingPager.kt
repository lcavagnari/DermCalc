package it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import android.app.Application
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.onboardingScreens
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme


/**
 * Horizontal pager that renders one [OnBoardItem] per onboarding page.
 *
 * @param pagerState state object controlling the current page and scroll position.
 * @param modifier modifier applied to the underlying [HorizontalPager].
 * @param userScrollEnabled whether the user can swipe between pages. Defaults to true.
 */
@Composable
fun OnboardingPager(pagerState: PagerState, modifier: Modifier, onboardingModel: OnboardingModel, userScrollEnabled: Boolean = true) {
    val dark = LocalDarkTheme.current
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        userScrollEnabled = userScrollEnabled,
    ) { page ->
        val screen = onboardingScreens[page]
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(if (dark) screen.backgroundDark else screen.backgroundLight),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Bottom padding reserves space for the chrome overlay (step indicator + button)
            // and the navigation bar so page content isn't hidden behind them.
            Box(
                Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 150.dp)
            ) {
                OnBoardItem(screen, onboardingModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PagerPreview() {
    val context = LocalContext.current
    val app = object : Application() { init { attachBaseContext(context) } }
    val vm = remember { OnboardingModel(app) }
    DermCalcTheme {
        OnboardingPager(
            rememberPagerState(initialPage = 0, pageCount = { onboardingScreens.size }),
            modifier = Modifier.fillMaxSize(),
            onboardingModel = vm
        )
    }
}