package it.lcavagnari.pdm.dermcalc.ui.preview

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import it.lcavagnari.pdm.dermcalc.AppMain
import it.lcavagnari.pdm.dermcalc.data.AppSettingsDao
import it.lcavagnari.pdm.dermcalc.data.AppSettingsEntity
import it.lcavagnari.pdm.dermcalc.data.ToolResultDao
import it.lcavagnari.pdm.dermcalc.data.ToolResultEntity
import it.lcavagnari.pdm.dermcalc.data.UserProfileDao
import it.lcavagnari.pdm.dermcalc.data.UserProfileEntity
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute
import it.lcavagnari.pdm.dermcalc.models.BmiResult
import it.lcavagnari.pdm.dermcalc.models.BodyScanModel
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus

/** Populates [ToolsModel] with sample BmiResult entries for preview composables. */
val previewBmiResults: (ToolsModel) -> Unit = { tm ->
    tm.addResult(BmiResult(weightKg = 70.0, heightCm = 175.0, score = 22.9))
    tm.addResult(
        BmiResult(
            weightKg = 85.0, heightCm = 175.0, score = 27.8,
            timestamp = today().date.minus(3, DateTimeUnit.DAY).atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 110.0, heightCm = 175.0, score = 35.9,
            timestamp = today().date.minus(10, DateTimeUnit.DAY)
                .atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 110.0, heightCm = 175.0, score = 35.9, timestamp = today().date.minus(
                10,
                DateTimeUnit.WEEK
            ).atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 110.0, heightCm = 175.0, score = 35.9, timestamp = today().date.minus(
                10,
                DateTimeUnit.MONTH
            ).atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 92.0, heightCm = 175.0, score = 30.1, timestamp = today().date.minus(
                1,
                DateTimeUnit.YEAR
            ).atTime(LocalTime.fromSecondOfDay(0))
        )
    )
    tm.addResult(
        BmiResult(
            weightKg = 78.0,
            heightCm = 175.0,
            score = 25.5,
            timestamp = today()
        )
    )
}

//  Preview wrapper functions
/**
 * Convenience overload that delegates to the 5-param version, rendering a full AppMain preview.
 *
 * @param screen starting destination for the preview navigation.
 * @param darkTheme whether to use dark theme in the preview.
 * @param setupOm callback to configure the onboarding model.
 * @param setupQm callback to configure the quote model.
 * @param setupTm callback to configure the tools model.
 * @param setupBm callback to configure the body scan model.
 */
@Composable
fun DermCalcPreview(
    screen: AppRoute = HomeRoute,
    darkTheme: Boolean = false,
    setupOm: (OnboardingModel) -> Unit = { it.finishOnboarding(); it.updateName("Alessandro Barbero ") },
    setupQm: (QuoteModel) -> Unit = { it.updateQuote() },
    setupTm: (ToolsModel) -> Unit = {},
    setupBm: (BodyScanModel) -> Unit = {}
) {
    DermCalcPreview(darkTheme,setupOm,setupQm,setupTm,setupBm) { vm, qm, tm, bm ->
        AppMain(quoteModel = qm, onboardingModel = vm, toolsModel = tm, bodyScanModel = bm, startingDestination = screen)
    }
}



/**
 * Core preview wrapper that creates stub DAOs, ViewModels, and wraps content in DermCalcTheme.
 *
 * @param darkTheme whether to use dark theme in the preview.
 * @param setupOm callback to configure the onboarding model.
 * @param setupQm callback to configure the quote model.
 * @param setupTm callback to configure the tools model.
 * @param setupBm callback to configure the body scan model.
 * @param content composable content to render within the preview theme.
 */
@Composable
fun DermCalcPreview(
    darkTheme: Boolean = false,
    setupOm: (OnboardingModel) -> Unit = { it.finishOnboarding() },
    setupQm: (QuoteModel) -> Unit = { it.updateQuote() },
    setupTm: (ToolsModel) -> Unit = {},
    setupBm: (BodyScanModel) -> Unit = {},
    content: @Composable (OnboardingModel, QuoteModel, ToolsModel, BodyScanModel) -> Unit
) {
    val context = LocalContext.current
    val app = remember { object : Application() { init { attachBaseContext(context) } } }
    val vm = remember {
        OnboardingModel(
            object : UserProfileDao {
                override suspend fun upsert(profile: UserProfileEntity) {}
                override fun getProfile() = MutableStateFlow<UserProfileEntity?>(null)
            },
            object : AppSettingsDao {
                override suspend fun upsert(settings: AppSettingsEntity) {}
                override fun getSettings() = MutableStateFlow<AppSettingsEntity?>(null)
            }
        )
    }.also { setupOm(it) }
    val qm = remember { QuoteModel(app) }.also { setupQm(it) }
    val tm = remember {
        ToolsModel(
            object : ToolResultDao {
                override suspend fun upsert(result: ToolResultEntity) {}
                override fun getAll() = MutableStateFlow<List<ToolResultEntity>>(emptyList())
                override suspend fun deleteById(id: Long) {}
                override suspend fun deleteAll() {}
            }
        )
    }.also { setupTm(it) }
    val bm = remember { BodyScanModel(app) }.also { setupBm(it) }

    DermCalcTheme(darkTheme = darkTheme, content = { content(vm,qm,tm,bm) })
}