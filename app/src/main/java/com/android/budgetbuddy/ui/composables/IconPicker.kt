package com.android.budgetbuddy.ui.composables

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Dock
import androidx.compose.material.icons.filled.Earbuds
import androidx.compose.material.icons.filled.EarbudsBattery
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.MoneyOffCsred
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.StoreMallDirectory
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.budgetbuddy.R

enum class IconsList(val icon: ImageVector) {
    Add(Icons.Filled.Add),
    AddChart(Icons.Filled.AddChart),
    AttachMoney(Icons.Filled.AttachMoney),
    AutoAwesome(Icons.Filled.AutoAwesome),
    AutoAwesomeMosaic(Icons.Filled.AutoAwesomeMosaic),
    AutoAwesomeMotion(Icons.Filled.AutoAwesomeMotion),
    AutoFixHigh(Icons.Filled.AutoFixHigh),
    AutoFixNormal(Icons.Filled.AutoFixNormal),
    Money(Icons.Filled.Money),
    MoneyOff(Icons.Filled.MoneyOff),
    MoneyOffCsred(Icons.Filled.MoneyOffCsred),
    RequestQuote(Icons.Filled.RequestQuote),
    Savings(Icons.Filled.Savings),
    AddShoppingCart(Icons.Filled.AddShoppingCart),
    Backpack(Icons.Filled.Backpack),
    Checkroom(Icons.Filled.Checkroom),
    DinnerDining(Icons.Filled.DinnerDining),
    EmojiFoodBeverage(Icons.Filled.EmojiFoodBeverage),
    Fastfood(Icons.Filled.Fastfood),
    FoodBank(Icons.Filled.FoodBank),
    LocalDining(Icons.Filled.LocalDining),
    LocalDrink(Icons.Filled.LocalDrink),
    LocalPizza(Icons.Filled.LocalPizza),
    LocalShipping(Icons.Filled.LocalShipping),
    LunchDining(Icons.Filled.LunchDining),
    Nightlife(Icons.Filled.Nightlife),
    Restaurant(Icons.Filled.Restaurant),
    RestaurantMenu(Icons.Filled.RestaurantMenu),
    RiceBowl(Icons.Filled.RiceBowl),
    ShoppingCart(Icons.Filled.ShoppingCart),
    Store(Icons.Filled.Store),
    Storefront(Icons.Filled.Storefront),
    StoreMallDirectory(Icons.Filled.StoreMallDirectory),
    WineBar(Icons.Filled.WineBar),
    Computer(Icons.Filled.Computer),
    DesktopWindows(Icons.Filled.DesktopWindows),
    DeveloperBoard(Icons.Filled.DeveloperBoard),
    DeviceHub(Icons.Filled.DeviceHub),
    Devices(Icons.Filled.Devices),
    Dock(Icons.Filled.Dock),
    Earbuds(Icons.Filled.Earbuds),
    EarbudsBattery(Icons.Filled.EarbudsBattery),
    East(Icons.Filled.East),
    Eco(Icons.Filled.Eco),
    ElectricBike(Icons.Filled.ElectricBike),
    ElectricCar(Icons.Filled.ElectricCar),
    ElectricMoped(Icons.Filled.ElectricMoped),
    ElectricRickshaw(Icons.Filled.ElectricRickshaw),
    ElectricScooter(Icons.Filled.ElectricScooter),
    Pets(Icons.Filled.Pets),
    BugReport(Icons.Filled.BugReport),
    ChildCare(Icons.Filled.ChildCare);

}

@Composable
fun IconPicker(
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(selectedIcon) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = filter,
            onValueChange = {
                expanded = true
                filter = it
            },
            label = { Text(stringResource(id = R.string.choose_an_icon)) },
            trailingIcon = {
                Column(
                    modifier = Modifier.clickable {
                        expanded = true

                        Log.d("Pippo", "Espansa")
                    },
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, "Icon")
                }
            }
        )

        IconList(
            expanded = expanded,
            onSelected = {
                filter = it
                Log.d("Pippo", "Chiusa")
                expanded = false
                onIconSelected(filter)
            },
            filter = filter
        )
    }
}

@Composable
fun IconList(
    expanded: Boolean,
    onSelected: (String) -> Unit,
    filter: String = ""
) {
    if (!expanded) {
        return
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),

        contentColor = MaterialTheme.colorScheme.primary,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)

    ) {
        LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
            this.items(
                items = IconsList.entries.filter {
                    it.name
                        .lowercase()
                        .contains(filter.lowercase())
                },
                itemContent = { icon ->
                    Row(
                        modifier = Modifier.clickable {
                            onSelected(icon.name)
                        }
                    ) {
                        Icon(icon.icon, icon.name)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(icon.name)
                    }
                }
            )
        }
    }

}

@Composable
@Preview
fun IconPickerPreview() {
    var icon by remember { mutableStateOf(IconsList.Money) }
    IconPicker(
        selectedIcon = icon.name,
        onIconSelected = {
            icon = IconsList.valueOf(it)
        }
    )
}
