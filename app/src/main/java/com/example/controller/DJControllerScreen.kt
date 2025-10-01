package com.example.controller

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// --- Main Composable Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DJControllerScreen(viewModel: DJViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val midiDeviceName by viewModel.midiDeviceName.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A1A1A) // Dark charcoal background
    ) {
        Column {
            // Top Status Bar
            TopAppBar(
                title = { Text("DJ Controller", color = Color.White) },
                actions = {
                    Text(
                        text = "Device: ${midiDeviceName ?: "Not Connected"}",
                        color = if (midiDeviceName != null) Color(0xFF4CAF50) else Color(0xFFFFC107),
                        modifier = Modifier.padding(end = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF101010))
            )

            // Main Controller Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Deck A
                Deck(
                    modifier = Modifier.weight(3f),
                    deckState = uiState.deckA,
                    onPlayToggle = { viewModel.setPlayState(0, !uiState.deckA.isPlaying) },
                    onCueToggle = { viewModel.setCueState(0) },
                    onJogWheelMove = { delta -> viewModel.setJogWheel(0, delta) },
                    onPitchChange = { value -> viewModel.setPitch(0, value) },
                    onPadClick = { padNote -> viewModel.sendPadAction(0, padNote) }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Center Mixer
                Mixer(
                    modifier = Modifier.weight(2f),
                    uiState = uiState,
                    onVolumeChange = { deck, value -> viewModel.setVolume(deck, value) },
                    onEqChange = { deck, band, value -> viewModel.setEq(deck, band, value) },
                    onCrossfaderChange = { value -> viewModel.setCrossfader(value) }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Deck B
                Deck(
                    modifier = Modifier.weight(3f),
                    deckState = uiState.deckB,
                    onPlayToggle = { viewModel.setPlayState(1, !uiState.deckB.isPlaying) },
                    onCueToggle = { viewModel.setCueState(1) },
                    onJogWheelMove = { delta -> viewModel.setJogWheel(1, delta) },
                    onPitchChange = { value -> viewModel.setPitch(1, value) },
                    onPadClick = { padNote -> viewModel.sendPadAction(1, padNote) }
                )
            }
        }
    }
}


// --- UI Components ---

@Composable
fun Deck(
    modifier: Modifier = Modifier,
    deckState: DeckState,
    onPlayToggle: () -> Unit,
    onCueToggle: () -> Unit,
    onJogWheelMove: (Float) -> Unit,
    onPitchChange: (Float) -> Unit,
    onPadClick: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(3f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JogWheel(onJogWheelMove = onJogWheelMove)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularButton("CUE", onCueToggle, Color(0xFFFFA000))
                CircularButton(
                    if (deckState.isPlaying) "❚❚" else "►",
                    onPlayToggle,
                    if (deckState.isPlaying) Color(0xFFD32F2F) else Color(0xFF388E3C),
                    isActivated = deckState.isPlaying
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            PerformancePads(onPadClick = onPadClick)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TEMPO", color = Color.Gray, fontSize = 10.sp)
            VerticalSlider(
                value = deckState.pitch,
                onValueChange = onPitchChange,
                valueRange = -8f..8f,
                modifier = Modifier.height(250.dp)
            )
        }
    }
}

@Composable
fun Mixer(
    modifier: Modifier = Modifier,
    uiState: ControllerUiState,
    onVolumeChange: (Int, Float) -> Unit,
    onEqChange: (Int, EqBand, Float) -> Unit,
    onCrossfaderChange: (Float) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF252525), RoundedCornerShape(8.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Deck A Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                EqKnob("HI", uiState.deckA.eq.high) { onEqChange(0, EqBand.HIGH, it) }
                EqKnob("MID", uiState.deckA.eq.mid) { onEqChange(0, EqBand.MID, it) }
                EqKnob("LOW", uiState.deckA.eq.low) { onEqChange(0, EqBand.LOW, it) }
                Spacer(modifier = Modifier.height(16.dp))
                Text("VOL A", color = Color.Gray, fontSize = 10.sp)
                VerticalSlider(
                    value = uiState.deckA.volume,
                    onValueChange = { onVolumeChange(0, it) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Deck B Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                EqKnob("HI", uiState.deckB.eq.high) { onEqChange(1, EqBand.HIGH, it) }
                EqKnob("MID", uiState.deckB.eq.mid) { onEqChange(1, EqBand.MID, it) }
                EqKnob("LOW", uiState.deckB.eq.low) { onEqChange(1, EqBand.LOW, it) }
                Spacer(modifier = Modifier.height(16.dp))
                Text("VOL B", color = Color.Gray, fontSize = 10.sp)
                VerticalSlider(
                    value = uiState.deckB.volume,
                    onValueChange = { onVolumeChange(1, it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Slider(
            value = uiState.crossfader,
            onValueChange = onCrossfaderChange,
            valueRange = -1f..1f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color(0xFF555555),
                inactiveTrackColor = Color(0xFF555555)
            )
        )
    }
}

@Composable
fun PerformancePads(onPadClick: (Int) -> Unit) {
    val pads = listOf(
        "HOT CUE" to MidiConstants.PAD_1, "PAD FX" to MidiConstants.PAD_2, "LOOP" to MidiConstants.PAD_3, "SAMPLER" to MidiConstants.PAD_4,
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pads.forEach { (label, note) ->
                PadButton(text = label, onClick = { onPadClick(note) }, modifier = Modifier.weight(1f))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (pads.indices).forEach { i ->
                PadButton(text = "", onClick = { onPadClick(MidiConstants.PAD_1 + i + 4) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PadButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF333333),
            contentColor = Color.White
        ),
        modifier = modifier.aspectRatio(1.5f),
        contentPadding = PaddingValues(4.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 10.sp)
    }
}


@Composable
fun JogWheel(onJogWheelMove: (Float) -> Unit) {
    var previousAngle by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF424242), Color(0xFF212121)),
                )
            )
            .border(2.dp, Color(0xFF616161), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        previousAngle = atan2(
                            y = size.height / 2 - offset.y,
                            x = offset.x - size.width / 2
                        ) * (180f / Math.PI.toFloat())
                    },
                    onDrag = { change, _ ->
                        val currentAngle = atan2(
                            y = size.height / 2 - change.position.y,
                            x = change.position.x - size.width / 2
                        ) * (180f / Math.PI.toFloat())

                        var angleDiff = currentAngle - previousAngle
                        if (angleDiff > 180) angleDiff -= 360
                        if (angleDiff < -180) angleDiff += 360

                        onJogWheelMove(angleDiff)
                        previousAngle = currentAngle
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.4f)
                .background(Color(0xFF333333), CircleShape)
        )
    }
}

@Composable
fun EqKnob(label: String, value: Float, onValueChange: (Float) -> Unit) {
    var rotation by remember(value) { mutableStateOf((value * 270f) - 135f) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White, fontSize = 12.sp)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        rotation = (rotation - dragAmount.y).coerceIn(-135f, 135f)
                        onValueChange((rotation + 135f) / 270f)
                        change.consume()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val angleRad = (rotation - 90) * (Math.PI / 180f).toFloat()
                val lineLength = size.width / 2
                val end = Offset(
                    x = center.x + lineLength * cos(angleRad),
                    y = center.y + lineLength * sin(angleRad)
                )
                drawLine(Color(0xFF00BCD4), center, end, strokeWidth = 6f)
            }
        }
    }
}

@Composable
fun CircularButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    isActivated: Boolean = false
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActivated) color else Color(0xFF444444),
            contentColor = Color.White
        ),
        modifier = Modifier.size(70.dp),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Text(text, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
    }
}

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        modifier = modifier.width(100.dp),
        colors = SliderDefaults.colors(
            thumbColor = Color(0xFF00BCD4),
            activeTrackColor = Color(0xFF00BCD4),
            inactiveTrackColor = Color(0xFF444444)
        )
    )
}