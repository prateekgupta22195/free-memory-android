import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pg.cloudcleaner.ui.DarkThemeColors
import com.pg.cloudcleaner.ui.LightThemeColors
import com.pg.cloudcleaner.ui.Shapes
import com.pg.cloudcleaner.ui.Typography

private val DarkColorPalette = darkColors(
    primary = DarkThemeColors.primary,
    primaryVariant = DarkThemeColors.primaryVariant,
    onPrimary = DarkThemeColors.onPrimary,
    onBackground = Color(0XFFFFFFFF)

)

private val LightColorPalette = lightColors(
    primary = LightThemeColors.primary,
    primaryVariant = LightThemeColors.primaryVariant,
    onPrimary = LightThemeColors.onPrimary,

)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
