Android MIDI DJ Controller
This project is a modern, tablet-optimized Android application that functions as a professional MIDI DJ controller. It does not process audio itself; instead, it sends MIDI signals over a USB-C cable to control desktop DJ software like Serato DJ Pro, Traktor Pro, or Virtual DJ.

Features
Two-Deck Control: Full control over two virtual decks.

Standard DJ Layout: Includes Play/Pause, Cue, Jog Wheels, Pitch Faders, 3-Band EQ, and Volume faders for each deck.

Central Mixer: A responsive crossfader for smooth transitions between decks.

MIDI Over USB: Plug-and-play connection to a PC or Mac. The app will appear as a standard MIDI device.

Responsive UI: Built with Jetpack Compose, the interface is clean, intuitive, and optimized for tablet screens.

Extensible Code: The modular architecture makes it easy to add new controls or customize the MIDI mappings.

Setup and Usage
Follow these steps to connect the app to your computer and control your DJ software.

Prerequisites
An Android tablet running Android 6.0 (Marshmallow) or higher.

A USB-C to USB-A or USB-C to USB-C cable.

DJ software installed on your PC or Mac (e.g., Serato, Traktor, Virtual DJ).

Step 1: Enable MIDI on your Android Device
Connect your Android tablet to your computer using the USB cable.

A notification will appear on your tablet regarding the USB connection. Tap on it.

In the USB options that appear, select MIDI. This is the most important step. Your tablet will now be recognized by your computer as a MIDI input device.

Step 2: Configure your DJ Software
You now need to "map" the controls from the Android app to the functions in your DJ software. Every button, fader, and knob on the app sends a unique MIDI message. You just have to tell your software what to do when it receives that message.

The general process is similar for most DJ software:

Open your DJ software's Settings or Preferences.

Find the MIDI or Controller Mapping section.

Enter "MIDI Learn" or "Mapping" mode.

Click on a function you want to map in the software (e.g., the Play button for Deck 1).

Press the corresponding button on the Android app (e.g., the PLAY button on Deck A).

The software should now recognize the MIDI signal and assign the control.

Repeat this process for all the controls you want to use (faders, knobs, etc.).

Save your custom mapping profile in the software.

Default MIDI Mappings
The app sends messages on three different MIDI channels:

Deck A: Channel 1

Deck B: Channel 2

Mixer (Crossfader): Channel 3

You can find the specific Note and CC numbers used for each control in the MidiConstants.kt file. This is useful for manual mapping or debugging.

Building from Source
This project is a standard Android Studio project.

Clone the repository.

Open the project in Android Studio.

Build and run on your physical Android device. An emulator will not work as it cannot connect as a USB MIDI device to your host machine.

Enjoy your new portable DJ controller!