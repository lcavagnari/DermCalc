package it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.onboardingScreens


/**
 * Horizontal pager that renders one [OnBoardItem] per onboarding page.
 *
 * @param pagerState - state object controlling the current page and scroll position.
 * @param modifier - modifier applied to the underlying [HorizontalPager].
 * @param userScrollEnabled - whether the user can swipe between pages. Defaults to true.
 */
@Composable
fun OnboardingPager(pagerState: PagerState, modifier: Modifier, userScrollEnabled: Boolean = true) {
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = userScrollEnabled,
        modifier = modifier.fillMaxWidth()
    ) { page ->
        OnBoardItem(onboardingScreens[page])
    }
}

@Preview(showBackground = true)
@Composable
fun PagerPreview() {
    OnboardingPager(
        rememberPagerState(initialPage = 0, pageCount = { onboardingScreens.size }),
        modifier = Modifier.fillMaxSize()
    )
}