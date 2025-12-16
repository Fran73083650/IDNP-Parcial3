package com.example.parcial3idnp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parcial3idnp.data.local.ActivityEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun ActivityCard(
    activity: ActivityEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dueDate = LocalDate.parse(activity.dueDate)
    val dueTime = activity.dueTime?.let { LocalTime.parse(it) }

    // COMPARACIÓN CORRECTA: Si tiene hora, usar LocalDateTime
    val now = LocalDateTime.now()
    val dueDateTime = if (dueTime != null) {
        LocalDateTime.of(dueDate, dueTime)
    } else {
        LocalDateTime.of(dueDate, LocalTime.MAX) // Sin hora = fin del día
    }

    // Calcular diferencia en minutos para precisión
    val minutesUntilDue = ChronoUnit.MINUTES.between(now, dueDateTime)
    val hoursUntilDue = minutesUntilDue / 60
    val daysUntilDue = minutesUntilDue / (60 * 24)

    // Determinar color según urgencia
    val urgencyColor = when {
        minutesUntilDue < 0 -> MaterialTheme.colorScheme.error
        hoursUntilDue < 24 -> MaterialTheme.colorScheme.tertiary
        daysUntilDue <= 7 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }

    // Texto de urgencia más preciso
    val urgencyText = when {
        minutesUntilDue < 0 -> {
            val minutesOverdue = -minutesUntilDue
            when {
                minutesOverdue < 60 -> "Vencida hace $minutesOverdue min"
                minutesOverdue < 1440 -> "Vencida hace ${minutesOverdue / 60} hora${if (minutesOverdue / 60 != 1L) "s" else ""}"
                else -> "Vencida hace ${minutesOverdue / 1440} día${if (minutesOverdue / 1440 != 1L) "s" else ""}"
            }
        }
        minutesUntilDue < 60 -> "¡Vence en $minutesUntilDue minutos!"
        hoursUntilDue < 24 -> "Vence en ${hoursUntilDue}h ${minutesUntilDue % 60}min"
        daysUntilDue == 0L -> "¡Vence hoy!"
        daysUntilDue == 1L -> "Vence mañana"
        daysUntilDue <= 7 -> "Vence en $daysUntilDue días"
        else -> "Vence el ${dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (minutesUntilDue < 0) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Título y categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                AssistChip(
                    onClick = { },
                    label = { Text(activity.category, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = urgencyColor.copy(alpha = 0.2f),
                        labelColor = urgencyColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            if (activity.description.isNotEmpty()) {
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Indicador de urgencia (fecha)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = urgencyColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = urgencyText,
                    style = MaterialTheme.typography.bodySmall,
                    color = urgencyColor,
                    fontWeight = FontWeight.Medium
                )
            }

            // Mostrar hora si existe
            if (dueTime != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hora: ${dueTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Recordatorios mejorados (MÚLTIPLES)
            if (activity.reminders.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    activity.reminders.forEach { reminder ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "🔔 ${reminder.amount} ${reminder.unit.displayName()} antes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}