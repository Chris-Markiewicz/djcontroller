package com.example.controller

/**
 * Defines standard MIDI mappings for the controller.
 * These values need to be mapped inside your DJ software (Serato, Traktor, etc.).
 *
 * Channels:
 * - Deck A uses MIDI Channel 1 (index 0)
 * - Deck B uses MIDI Channel 2 (index 1)
 * - Mixer uses MIDI Channel 3 (index 2)
 */
object MidiConstants {
    // --- NOTE ON/OFF MESSAGES (for buttons) ---
    const val PLAY_PAUSE = 20 // e.g., MIDI Note C#1
    const val CUE = 21        // e.g., MIDI Note D1
    const val SYNC = 22       // e.g., MIDI Note D#1
    const val FX_1_TOGGLE = 23 // e.g., MIDI Note E1

    // Performance Pads (Notes C2 to G#2)
    const val PAD_1 = 36
    const val PAD_2 = 37
    const val PAD_3 = 38
    const val PAD_4 = 39
    const val PAD_5 = 40
    const val PAD_6 = 41
    const val PAD_7 = 42
    const val PAD_8 = 43

    // --- CONTROL CHANGE (CC) MESSAGES (for faders, knobs, jog wheel) ---
    const val VOLUME = 7      // Standard CC for channel volume
    const val JOG_WHEEL = 10
    const val PITCH = 11
    const val CROSSFADER = 12
    const val EQ_LOW = 13
    const val EQ_MID = 14
    const val EQ_HIGH = 15
}