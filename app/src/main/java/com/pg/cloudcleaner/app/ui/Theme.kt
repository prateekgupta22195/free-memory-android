import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pg.cloudcleaner.app.ui.DarkThemeColors
import com.pg.cloudcleaner.app.ui.LightThemeColors
import com.pg.cloudcleaner.app.ui.Typography

//private val DarkColorPalette = darkColors(
//    primary = DarkThemeColors.primary,
//    primaryVariant = DarkThemeColors.primaryVariant,
//    onPrimary = DarkThemeColors.onPrimary,
//    onBackground = Color(0XFFFFFFFF)
//
//)
//
//private val LightColorPalette = lightColors(
//    primary = LightThemeColors.primary,
//    primaryVariant = LightThemeColors.primaryVariant,
//    onPrimary = LightThemeColors.onPrimary,
//
//)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {

    val colors = if (darkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
//        LightColorPalette
    }


    MaterialTheme(
        colorScheme = colors,
//        typography = Typography,
//        shapes = Shapes,
        content = content,
    )
}
