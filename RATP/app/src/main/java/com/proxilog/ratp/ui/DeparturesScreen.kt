package com.proxilog.ratp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.proxilog.ratp.data.Departure
import com.proxilog.ratp.data.Direction
import com.proxilog.ratp.data.RerData
import com.proxilog.ratp.data.RerLine
import com.proxilog.ratp.ui.theme.RATPTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Button

@Composable
fun DeparturesScreen(modifier: Modifier = Modifier) {
    var lineIndex by rememberSaveable { mutableStateOf(0) }
    val line = RerData.lines[lineIndex]
    var station by rememberSaveable(lineIndex) { mutableStateOf(line.stations.first()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
  
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StationSelector(
                stations = line.stations,
                selected = station,
                onSelected = { station = it },
            )
            DirectionCard(line, station, line.direction1)
            DirectionCard(line, station, line.direction2)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationSelector(
    stations: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember(selected) { mutableStateOf(selected) }
    val filtered = if (query == selected) stations
    else stations.filter { it.contains(query, ignoreCase = true) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true
            },
            label = { Text("Gare de départ") },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded && filtered.isNotEmpty(),
            onDismissRequest = {
                expanded = false
                query = selected
            },
        ) {
            filtered.forEach { name ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelected(name)
                        query = name
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun DirectionCard(line: RerLine, station: String, direction: Direction) {
    var displayAll by remember {mutableStateOf(false)}
    val departures = RerData.departures(line, station, direction)
    val departuresVisible = if (displayAll)  { departures} else { departures.take(2)}
    val now = LocalTime.now()
    

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Direction ${direction.label}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = direction.termini.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.size(8.dp))
            departuresVisible.forEachIndexed { index, departure ->
                if (index > 0) HorizontalDivider()
                DepartureRow(departure, now)
                
            }
            Button(
                    onClick = {
                       displayAll = !displayAll 
                    },
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        if (displayAll) "Afficher moins" else "Afficher plus d'horaires"
                    )
                }
        }
    }
}

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
private fun DepartureRow(departure: Departure, now: LocalTime) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = departure.mission,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(56.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(departure.destination, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Voie ${departure.platform} · ${now.plusMinutes(departure.minutes.toLong()).format(timeFormatter)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = if (departure.minutes == 0) "À quai" else "${departure.minutes} min",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun LineBadge(line: RerLine) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(line.color, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(line.id, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
private fun DeparturesScreenPreview() {
    RATPTheme {
        DeparturesScreen()
    }
}
