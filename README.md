# Android Touch Simulator 📱

An Android app built with Kotlin that allows you to simulate touch screen interactions using volume buttons. Set a custom touch position and trigger it anywhere in the system by pressing the volume down button.

## ✨ Features

- **Volume Button Control**: Press Volume Down to simulate a touch at your set position
- **Custom Touch Positioning**: Tap anywhere on screen to set your desired touch coordinates
- **System-Wide Functionality**: Works across all apps and screens
- **Persistent Settings**: Your touch position is saved between app sessions
- **Real-Time Status**: Monitor accessibility service and permission status
- **Modern UI**: Clean Material Design interface

## 🚀 How It Works

1. **Set Position**: Open the app and tap "Set Touch Position" to choose where you want touches to occur
2. **Enable Service**: Tap "Enable Accessibility Service" and enable the Touch Simulator service in Android settings
3. **Grant Permissions**: Allow overlay permissions when prompted
4. **Use Anywhere**: Press Volume Down from any app or screen to simulate a touch at your set position

## 📋 Requirements

- **Android 7.0 (API 24) or higher**
- Accessibility service permissions
- Overlay/Draw over other apps permission

## 🛠️ Installation

### Option 1: Build from Source
1. Clone this repository:
   ```bash
   git clone https://github.com/LadyHannelore/Android-touch.git
   ```
2. Open the project in Android Studio
3. Build and install the APK to your device

### Option 2: Install APK
1. Download the APK from the [Releases](https://github.com/LadyHannelore/Android-touch/releases) page
2. Enable "Install from Unknown Sources" in your device settings
3. Install the APK

## ⚙️ Setup Instructions

### 1. Install the App
Install the app using one of the methods above.

### 2. Enable Accessibility Service
1. Open the Touch Simulator app
2. Tap "Enable Accessibility Service"
3. In Android Settings → Accessibility, find "Touch Simulator"
4. Toggle it ON and confirm

### 3. Grant Overlay Permission
1. When prompted, grant "Display over other apps" permission
2. Or manually go to Settings → Apps → Touch Simulator → Display over other apps → Allow

### 4. Set Touch Position
1. In the app, tap "Set Touch Position"
2. Tap anywhere on the screen where you want touches to occur
3. Tap "Confirm" to save the position

## 🎯 Usage

Once set up, press **Volume Down** from anywhere on your device to simulate a touch at your chosen position. This works:

- ✅ In any app
- ✅ On the home screen  
- ✅ In games
- ✅ During calls
- ✅ When screen is locked (if accessibility allows)

## 🏗️ Technical Details

### Architecture
- **MainActivity**: Main app interface and status monitoring
- **PositionSelectorActivity**: Touch position selection screen
- **VolumeKeyService**: Accessibility service for volume key detection
- **TouchSimulationService**: Background service for touch management

### Key Technologies
- **Kotlin**: Primary programming language
- **Android Accessibility Service**: For system-wide volume key interception
- **Gesture API**: For precise touch simulation
- **SharedPreferences**: For persistent settings storage
- **Material Design**: For modern UI components

### Permissions Used
```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.ACCESSIBILITY_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

## 🔧 Development

### Building the Project
```bash
./gradlew assembleDebug
```

### Running Tests
```bash
./gradlew test
```

### Code Structure
```
app/src/main/java/com/touchsimulator/app/
├── MainActivity.kt                 # Main app screen
├── PositionSelectorActivity.kt    # Position selection
├── VolumeKeyService.kt           # Accessibility service
└── TouchSimulationService.kt     # Touch simulation logic
```

## 🐛 Troubleshooting

### Touch Not Working
1. ✅ Ensure accessibility service is enabled
2. ✅ Check overlay permission is granted
3. ✅ Verify a touch position has been set
4. ✅ Try restarting the app

### Volume Buttons Still Change Volume
- The app only intercepts Volume Down when accessibility service is properly enabled
- Volume Up continues to work normally

### Position Not Accurate
- Re-set your touch position for better accuracy
- Ensure you're testing on the same screen orientation where you set the position

## 📱 Compatibility

### Tested On
- Android 7.0 - 14.0
- Various device manufacturers (Samsung, Google, OnePlus, etc.)
- Different screen sizes and resolutions

### Known Limitations
- Requires accessibility service (security feature)
- May not work in some secure apps (banking apps, etc.)
- Position accuracy may vary on different screen densities

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ⚠️ Disclaimer

This app is designed for accessibility and automation purposes. Please use responsibly and in accordance with the terms of service of other applications. The developers are not responsible for any misuse of this application.

## 🙏 Acknowledgments

- Android Accessibility Framework
- Material Design Components
- Kotlin Programming Language

---

**Made with ❤️ for accessibility and convenience**
