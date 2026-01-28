package com.msanwr.coursemate.jetpack.dev.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceFormSheet(
    homeViewModel: HomeViewModel,
    onSearchClicked: (String, String, Int) -> Unit
) {
    // State -> user input
    var selectedSubCategory by remember { mutableStateOf(homeViewModel.subCategoryOptions[0]) }
    var selectedCourseType by remember { mutableStateOf(homeViewModel.courseTypeOptions[0]) }
    var selectedDuration by remember { mutableIntStateOf(homeViewModel.durationOptions[0]) }

    var isSubCategoryExpanded by remember { mutableStateOf(false) }
    var isCourseTypeExpanded by remember { mutableStateOf(false) }
    var isDurationExpanded by remember { mutableStateOf(false) }


    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Formulir Preferensi",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown: Minat (Sub-Category)
            ExposedDropdownMenuBox(
                expanded = isSubCategoryExpanded,
                onExpandedChange = { isSubCategoryExpanded = !isSubCategoryExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = selectedSubCategory,
                    onValueChange = { },
                    label = { Text("Pilih Topik yang Anda Minati") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSubCategoryExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = isSubCategoryExpanded,
                    onDismissRequest = { isSubCategoryExpanded = false }
                ) {
                    homeViewModel.subCategoryOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = selectionOption) },
                            onClick = {
                                selectedSubCategory = selectionOption
                                isSubCategoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown: Tipe Kursus
            ExposedDropdownMenuBox(
                expanded = isCourseTypeExpanded,
                onExpandedChange = { isCourseTypeExpanded = !isCourseTypeExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = selectedCourseType,
                    onValueChange = { },
                    label = { Text("Pilih Tipe Kursus yang Anda Cari") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCourseTypeExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = isCourseTypeExpanded,
                    onDismissRequest = { isCourseTypeExpanded = false }
                ) {
                    homeViewModel.courseTypeOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = selectionOption) },
                            onClick = {
                                selectedCourseType = selectionOption
                                isCourseTypeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown: Durasi
            ExposedDropdownMenuBox(
                expanded = isDurationExpanded,
                onExpandedChange = { isDurationExpanded = !isDurationExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = "$selectedDuration Minggu",
                    onValueChange = { },
                    label = { Text("Pilih Durasi Kursus yang Anda Inginkan (dalam minggu)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDurationExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = isDurationExpanded,
                    onDismissRequest = { isDurationExpanded = false }
                ) {
                    homeViewModel.durationOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = "$selectionOption Minggu") },
                            onClick = {
                                selectedDuration = selectionOption
                                isDurationExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSearchClicked(selectedSubCategory, selectedCourseType, selectedDuration)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dapatkan Rekomendasi")
            }
        }
    }
}