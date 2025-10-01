package com.example.controller

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MidiController(context: Context) {
    private val midiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager
    private var midiDevice: MidiDevice? = null
    private var inputPort: MidiInputPort? = null

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName = _connectedDeviceName.asStateFlow()

    private val deviceCallback = object : MidiManager.DeviceCallback() {
        override fun onDeviceAdded(device: MidiDeviceInfo?) {
            super.onDeviceAdded(device)
            Log.d("MidiController", "MIDI Device Added: $device")
            // Automatically connect to the first available device
            if (midiDevice == null && device != null) {
                connectToDevice(device)
            }
        }

        override fun onDeviceRemoved(device: MidiDeviceInfo?) {
            super.onDeviceRemoved(device)
            Log.d("MidiController", "MIDI Device Removed: $device")
            if (midiDevice?.info?.id == device?.id) {
                stop()
            }
        }
    }

    fun start() {
        // First, unregister any existing callbacks to be safe
        midiManager.unregisterDeviceCallback(deviceCallback)
        // Then, register the callback
        midiManager.registerDeviceCallback(deviceCallback, Handler(Looper.getMainLooper()))
        // Check for already connected devices
        midiManager.devices.firstOrNull()?.let { connectToDevice(it) }
    }

    private fun connectToDevice(deviceInfo: MidiDeviceInfo) {
        midiManager.openDevice(deviceInfo, { device ->
            midiDevice = device
            // Port 0 is usually the one you want for output
            inputPort = device.openInputPort(0)
            _connectedDeviceName.value = device.info.properties.getString(MidiDeviceInfo.PROPERTY_NAME)
            Log.i("MidiController", "Successfully connected to ${connectedDeviceName.value}")
        }, Handler(Looper.getMainLooper()))
    }

    fun stop() {
        // Unregister the callback to prevent memory leaks
        midiManager.unregisterDeviceCallback(deviceCallback)
        inputPort?.close()
        midiDevice?.close()
        inputPort = null
        midiDevice = null
        _connectedDeviceName.value = null
        Log.i("MidiController", "MIDI connection closed.")
    }

    /**
     * Sends a MIDI Note On message.
     * @param channel The MIDI channel (0-15). We use 0 for Deck A, 1 for Deck B.
     * @param note The MIDI note number (0-127).
     * @param velocity The velocity (0-127). 0 is equivalent to Note Off.
     */
    fun sendNoteOn(channel: Int, note: Int, velocity: Int) {
        if (inputPort == null) return
        val buffer = ByteArray(3)
        buffer[0] = (0x90 + channel).toByte() // 0x90 is Note On
        buffer[1] = note.toByte()
        buffer[2] = velocity.toByte()
        inputPort?.send(buffer, 0, buffer.size)
    }

    /**
     * Sends a MIDI Control Change (CC) message.
     * @param channel The MIDI channel (0-15).
     * @param control The CC number (0-127).
     * @param value The CC value (0-127).
     */
    fun sendControlChange(channel: Int, control: Int, value: Int) {
        if (inputPort == null) return
        val buffer = ByteArray(3)
        buffer[0] = (0xB0 + channel).toByte() // 0xB0 is Control Change
        buffer[1] = control.toByte()
        buffer[2] = value.toByte()
        inputPort?.send(buffer, 0, buffer.size)
    }
}