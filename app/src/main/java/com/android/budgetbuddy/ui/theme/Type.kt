package com.android.budgetbuddy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.android.budgetbuddy.R


object BudgetBuddyFont {
    val Inter = FontFamily(
        Font(R.font.inter),
        Font(R.font.inter_semibold, FontWeight.SemiBold),
        Font(R.font.inter_medium, FontWeight.Medium),
        Font(R.font.inter_extrabold, FontWeight.ExtraBold),
        Font(R.font.inter_bold, FontWeight.Bold)
    )
}

private val defaultTypography = Typography()
// Set of Material typography styles to start with
val Typography = Typography(
    /*bodyLarge = TextStyle(
        fontFamily = BudgetBuddyFont.Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
*/
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */

    displayLarge = defaultTypography.displayLarge.copy(fontFamily = BudgetBuddyFont.Inter),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = BudgetBuddyFont.Inter),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = BudgetBuddyFont.Inter),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = BudgetBuddyFont.Inter),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = BudgetBuddyFont.Inter),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = BudgetBuddyFont.Inter),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = BudgetBuddyFont.Inter),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = BudgetBuddyFont.Inter),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = BudgetBuddyFont.Inter),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = BudgetBuddyFont.Inter),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = BudgetBuddyFont.Inter),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = BudgetBuddyFont.Inter),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = BudgetBuddyFont.Inter),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = BudgetBuddyFont.Inter),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = BudgetBuddyFont.Inter)
)