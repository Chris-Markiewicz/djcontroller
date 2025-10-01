# Android MIDI DJ Controller

This project is a modern, tablet-optimized Android application that functions as a professional MIDI DJ controller. It does not process audio itself; instead, it sends MIDI signals over a USB-C cable to control desktop DJ software like Serato DJ Pro, Traktor Pro, or Virtual DJ.

## Features

*   **Two-Deck Control:** Full control over two virtual decks.
*   **Standard DJ Layout:** Includes Play/Pause, Cue, Jog Wheels, Pitch Faders, 3-Band EQ, and Volume faders for each deck.
*   **Central Mixer:** A responsive crossfader for smooth transitions between decks.
*   **MIDI Over USB:** Plug-and-play connection to a PC or Mac. The app will appear as a standard MIDI device.
*   **Responsive UI:** Built with Jetpack Compose, the interface is clean, intuitive, and optimized for tablet screens.
*   **Extensible Code:** The modular architecture makes it easy to add new controls or customize the MIDI mappings.

## Setup and Usage

Follow these steps to connect the app to your computer and control your DJ software.

### Prerequisites

*   An Android tablet or phone running Android 6.0 (Marshmallow) or higher.
*   A USB-C to USB-A or USB-C to USB-C cable.
*   DJ software installed on your PC or Mac (e.g., Serato, Traktor, Virtual DJ).

### Step 1: Enable MIDI on your Android Device

1.  Connect your Android device to your computer using the USB cable.
2.  A notification will appear on your device regarding the USB connection. Tap on it.
3.  In the USB options that appear, select **MIDI**. This is the most important step. Your device will now be recognized by your computer as a MIDI input device.

### Step 2: Configure your DJ Software

You now need to "map" the controls from the Android app to the functions in your DJ software. Every button, fader, and knob on the app sends a unique MIDI message. You just have to tell your software what to do when it receives that message.

The general process is similar for most DJ software:

1.  Open your DJ software's **Settings** or **Preferences**.
2.  Find the **MIDI** or **Controller Mapping** section.
3.  Enter **"MIDI Learn"** or **"Mapping"** mode.
4.  Click on a function you want to map in the software (e.g., the Play button for Deck 1).
5.  Press the corresponding button on the Android app (e.g., the PLAY button on Deck A).
6.  The software should now recognize the MIDI signal and assign the control.
7.  Repeat this process for all the controls you want to use (faders, knobs, etc.).
8.  **Save** your custom mapping profile in the software.

## Default MIDI Mappings

The app sends messages on three different MIDI channels:

*   **Deck A:** Channel 1
*   **Deck B:** Channel 2
*   **Mixer (Crossfader):** Channel 3

You can find the specific Note and CC numbers used for each control in the `MidiConstants.kt` file. This is useful for manual mapping or debugging.

### Note On/Off Messages

| Control          | MIDI Number |
| ---------------- | ----------- |
| Play/Pause       | 20          |
| Cue              | 21          |
| Sync             | 22          |
| FX 1 Toggle      | 23          |
| Performance Pads | 36-43       |

### Control Change (CC) Messages

| Control          | MIDI Number |
| ---------------- | ----------- |
| Volume           | 7           |
| Jog Wheel        | 10          |
| Pitch            | 11          |
| Crossfader       | 12          |
| EQ Low           | 13          |
| EQ Mid           | 14          |
| EQ High          | 15          |

## Building from Source

This project is a standard Android Studio project.

1.  Clone the repository:
    ```bash
    git clone https://github.com/your-username/your-repository-name.git
    ```
2.  Open the project in Android Studio.
3.  Build and run on your physical Android device. An emulator will not work as it cannot connect as a USB MIDI device to your host machine.

## How it Works

This app is built entirely with modern Android development tools:

*   **Jetpack Compose:** The entire UI is built with Jetpack Compose, providing a reactive and modern user interface.
*   **Kotlin:** The app is written entirely in Kotlin.
*   **Android MIDI API:** The app uses the `android.media.midi` package to send MIDI messages to the connected computer.
*   **MVVM Architecture:** The app follows a Model-View-ViewModel architecture to separate concerns and make the code more maintainable.

## Contributing

Contributions are welcome! Please feel free to submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
