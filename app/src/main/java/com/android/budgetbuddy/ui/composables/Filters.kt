package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.android.budgetbuddy.R
import java.time.LocalDate


@Composable
fun ExpandableSectionContainer(
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    text: String = stringResource(id = R.string.filter),
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    content: @Composable () -> Unit = { Spacer(modifier = Modifier.height(20.dp)) }
) {
    Column(
        modifier = Modifier.padding(10.dp, 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.secondary)
                .clickable {
                    onExpandedChange()
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                style = textStyle,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(10.dp)
            )
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(10.dp)
            )
        }

        if (expanded) {
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                content()
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeDateFilter(
    enabled: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    startValue: LocalDate,
    onStartValueChange: (LocalDate) -> Unit,
    endValue: LocalDate,
    onEndValueChange: (LocalDate) -> Unit
) {
    // Date filter
    Row (
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(0.11f),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                Checkbox(
                    checked = enabled,
                    onCheckedChange = onSelectionChange,
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.fillMaxWidth(0.5f)) {
            Text(text = stringResource(id = R.string.from), style = MaterialTheme.typography.labelLarge)
            CustomDatePicker(
                value = startValue,
                onValueChange = onStartValueChange,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.to), style = MaterialTheme.typography.labelLarge)
            CustomDatePicker(
                value = endValue,
                onValueChange = onEndValueChange,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun TypeFilter(
    types: List<String>,
    selectedTypes: List<String>,
    onTypeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        types.forEach { type ->
            val selected = selectedTypes.contains(type)
            FilterChip(
                selected = selected,
                onClick = { onTypeSelected(type) },
                label = { Text(type) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    labelColor = MaterialTheme.colorScheme.onBackground,
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.padding(end = 5.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryFilter(
    categories: List<Pair<Int, String>>,
    selectedCategories: List<Pair<Int, String>>,
    onCategorySelected: (Pair<Int, String>) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.forEach { category ->
            val selected = selectedCategories.contains(category)
            FilterChip(
                selected = selected,
                onClick = { onCategorySelected(category) },
                label = { Text(category.second) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    labelColor = MaterialTheme.colorScheme.onBackground,
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.padding(end = 5.dp)
            )
        }
    }
}

@Composable
fun AmountFilter() {

}