package com.example.parcial3idnp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parcial3idnp.data.local.ActivityEntity
import com.example.parcial3idnp.data.local.ReminderItem
import com.example.parcial3idnp.data.local.ReminderUnit
import com.example.parcial3idnp.ui.viewmodel.ActivityViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    viewModel: ActivityViewModel,
    activityId: Int?, // ← CAMBIO: Recibe ID opcional
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var activityToEdit by remember { mutableStateOf<ActivityEntity?>(null) }
    var isLoading by remember { mutableStateOf(activityId != null) }

    // Cargar actividad si es edición
    LaunchedEffect(activityId) {
        if (activityId != null) {
            scope.launch {
                activityToEdit = viewModel.getActivityById(activityId)
                isLoading = false
            }
        }
    }

    // Estados del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Universidad") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var hasSpecificTime by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.of(12, 0)) }

    // Lista de recordatorios
    var reminders by remember { mutableStateOf<List<ReminderItem>>(emptyList()) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    // Llenar el formulario cuando se carga la actividad
    LaunchedEffect(activityToEdit) {
        activityToEdit?.let { activity ->
            title = activity.title
            description = activity.description
            selectedCategory = activity.category
            selectedDate = LocalDate.parse(activity.dueDate)
            hasSpecificTime = activity.dueTime != null
            if (activity.dueTime != null) {
                selectedTime = LocalTime.parse(activity.dueTime)
            }
            reminders = activity.reminders
        }
    }

    val categories = listOf("Universidad", "Casa", "Trabajo", "Otros")
    var expandedCategory by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
    )

    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = true
    )

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (activityId == null) "Nueva Actividad" else "Editar Actividad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (showError && it.isNotBlank()) showError = false
                },
                label = { Text("Título *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && title.isBlank(),
                supportingText = if (showError && title.isBlank()) {
                    { Text("El título es obligatorio") }
                } else null
            )

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Categoría
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            // Fecha
            OutlinedTextField(
                value = selectedDate.format(dateFormatter),
                onValueChange = {},
                label = { Text("Fecha de vencimiento *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, "Seleccionar fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Toggle hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Tiene hora específica?", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = hasSpecificTime, onCheckedChange = { hasSpecificTime = it })
            }

            // Hora
            if (hasSpecificTime) {
                OutlinedTextField(
                    value = selectedTime.format(timeFormatter),
                    onValueChange = {},
                    label = { Text("Hora") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.AccessTime, "Seleccionar hora")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider()

            // SECCIÓN DE RECORDATORIOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recordatorios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                FilledTonalButton(
                    onClick = { showAddReminderDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar")
                }
            }

            // Lista de recordatorios
            if (reminders.isEmpty()) {
                Text(
                    text = "Sin recordatorios configurados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                reminders.forEachIndexed { index, reminder ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${reminder.amount} ${reminder.unit.displayName()} antes",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            IconButton(
                                onClick = {
                                    reminders = reminders.toMutableList().apply { removeAt(index) }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón guardar
            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        val activity = ActivityEntity(
                            id = activityToEdit?.id ?: 0,
                            title = title.trim(),
                            description = description.trim(),
                            dueDate = selectedDate.toString(),
                            dueTime = if (hasSpecificTime) selectedTime.toString() else null,
                            category = selectedCategory,
                            reminders = reminders
                        )

                        if (activityId == null) {
                            viewModel.addActivity(activity)
                        } else {
                            viewModel.updateActivity(activity)
                        }

                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (activityId == null) "Guardar Actividad" else "Actualizar Actividad")
            }
        }
    }

    // DatePicker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePicker
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                TimePicker(state = timePickerState, modifier = Modifier.padding(16.dp))
            }
        )
    }

    // Dialog Agregar Recordatorio
    if (showAddReminderDialog) {
        AddReminderDialog(
            onDismiss = { showAddReminderDialog = false },
            onConfirm = { newReminder ->
                reminders = reminders + newReminder
                showAddReminderDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (ReminderItem) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf(ReminderUnit.MINUTES) }
    var expandedUnit by remember { mutableStateOf(false) }

    val units = listOf(
        ReminderUnit.MINUTES to "Minutos",
        ReminderUnit.HOURS to "Horas",
        ReminderUnit.DAYS to "Días"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Recordatorio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull() != null)) {
                            amount = it
                        }
                    },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = it }
                ) {
                    OutlinedTextField(
                        value = units.find { it.first == selectedUnit }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        units.forEach { (unit, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    selectedUnit = unit
                                    expandedUnit = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountInt = amount.toIntOrNull()
                    if (amountInt != null && amountInt > 0) {
                        onConfirm(ReminderItem(amountInt, selectedUnit))
                    }
                },
                enabled = amount.isNotEmpty() && (amount.toIntOrNull() ?: 0) > 0
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}