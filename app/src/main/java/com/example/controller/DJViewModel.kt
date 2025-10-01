package com.example.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Data classes for state management
data class DeckState(
    val isPlaying: Boolean = false,
    val volume: Float = 1.0f, // 0.0 to 1.0
    val pitch: Float = 0.0f, // -8.0 to 8.0
    val eq: EqState = EqState()
)

data class EqState(
    val low: Float = 0.5f, // 0.0 to 1.0
    val mid: Float = 0.5f, // 0.0 to 1.0
    val high: Float = 0.5f, // 0.0 to 1.0
)

enum class EqBand { HIGH, MID, LOW }

data class ControllerUiState(
    val deckA: DeckState = DeckState(),
    val deckB: DeckState = DeckState(),
    val crossfader: Float = 0.0f // -1.0 (Left) to 1.0 (Right)
)

class DJViewModel(private val midiController: MidiController) : ViewModel() {

    private val _uiState = MutableStateFlow(ControllerUiState())
    val uiState = _uiState.asStateFlow()

    val midiDeviceName = midiController.connectedDeviceName

    init {
        midiController.start()
    }

    // --- State Update Methods ---

    fun setPlayState(deckIndex: Int, isPlaying: Boolean) {
        _uiState.update { currentState ->
            if (deckIndex == 0) {
                currentState.copy(deckA = currentState.deckA.copy(isPlaying = isPlaying))
            } else {
                currentState.copy(deckB = currentState.deckB.copy(isPlaying = isPlaying))
            }
        }
        midiController.sendNoteOn(deckIndex, MidiConstants.PLAY_PAUSE, if (isPlaying) 127 else 0)
    }

    fun setCueState(deckIndex: Int) {
        // Cue is momentary, so we just send the MIDI message
        midiController.sendNoteOn(deckIndex, MidiConstants.CUE, 127)
        midiController.sendNoteOn(deckIndex, MidiConstants.CUE, 0) // And off immediately
    }

    fun setJogWheel(deckIndex: Int, delta: Float) {
        // We'll map the delta to a MIDI value.
        // A simple approach: positive delta is 1, negative is 127.
        // DJ software interprets this as "one tick forward" or "one tick back".
        val midiValue = if (delta > 0) 1 else 127
        midiController.sendControlChange(deckIndex, MidiConstants.JOG_WHEEL, midiValue)
    }

    fun setPitch(deckIndex: Int, value: Float) {
        _uiState.update { currentState ->
            if (deckIndex == 0) {
                currentState.copy(deckA = currentState.deckA.copy(pitch = value))
            } else {
                currentState.copy(deckB = currentState.deckB.copy(pitch = value))
            }
        }
        // Map pitch (-8 to +8) to MIDI (0-127). 0 pitch = 64 MIDI.
        val midiValue = ((value + 8f) / 16f * 127).toInt().coerceIn(0, 127)
        midiController.sendControlChange(deckIndex, MidiConstants.PITCH, midiValue)
    }

    fun setVolume(deckIndex: Int, value: Float) {
        _uiState.update { currentState ->
            if (deckIndex == 0) {
                currentState.copy(deckA = currentState.deckA.copy(volume = value))
            } else {
                currentState.copy(deckB = currentState.deckB.copy(volume = value))
            }
        }
        val midiValue = (value * 127).toInt()
        midiController.sendControlChange(deckIndex, MidiConstants.VOLUME, midiValue)
    }

    fun setEq(deckIndex: Int, band: EqBand, value: Float) {
        _uiState.update { currentState ->
            if (deckIndex == 0) {
                val newEq = when(band) {
                    EqBand.HIGH -> currentState.deckA.eq.copy(high = value)
                    EqBand.MID -> currentState.deckA.eq.copy(mid = value)
                    EqBand.LOW -> currentState.deckA.eq.copy(low = value)
                }
                currentState.copy(deckA = currentState.deckA.copy(eq = newEq))
            } else {
                val newEq = when(band) {
                    EqBand.HIGH -> currentState.deckB.eq.copy(high = value)
                    EqBand.MID -> currentState.deckB.eq.copy(mid = value)
                    EqBand.LOW -> currentState.deckB.eq.copy(low = value)
                }
                currentState.copy(deckB = currentState.deckB.copy(eq = newEq))
            }
        }
        val midiValue = (value * 127).toInt()
        val cc = when(band) {
            EqBand.HIGH -> MidiConstants.EQ_HIGH
            EqBand.MID -> MidiConstants.EQ_MID
            EqBand.LOW -> MidiConstants.EQ_LOW
        }
        midiController.sendControlChange(deckIndex, cc, midiValue)
    }

    fun setCrossfader(value: Float) {
        _uiState.update { it.copy(crossfader = value) }
        // Map crossfader (-1 to +1) to MIDI (0-127). 0 = 64.
        val midiValue = (((value + 1f) / 2f) * 127).toInt()
        midiController.sendControlChange(2, MidiConstants.CROSSFADER, midiValue) // Mixer channel often 2 or 3
    }

    fun sendPadAction(deckIndex: Int, padNote: Int) {
        // Pads are momentary, so send Note On then Note Off immediately
        midiController.sendNoteOn(deckIndex, padNote, 127)
        midiController.sendNoteOn(deckIndex, padNote, 0)
    }

    override fun onCleared() {
        midiController.stop()
        super.onCleared()
    }
}