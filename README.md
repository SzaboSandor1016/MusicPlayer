# MusicPlayer

A simple music player built for offline listening.
In addition to simply playing a music, users can create custom play queues featuring their favorite songs, 
manage custom and automatically created playlists like "Recently added" or "Favorites"

---

## Features

- **Music player**

  - Assemble custom play queues of songs even from multiple different playlists (Add, remove and reorder songs manually)
  - Modify the play queue to make (multiple) selected songs played right after the current one
  - Shuffle the order or set the repeat mode of playback
  - Add a song to "Favorites" directly from the player itself

- **Playlists and "All songs"**

  - Quickly access all the songs selecting the "All songs" option on the main screen (Synchronized with the device's storage automatically)
  - Create and manage custom playlists
  - See the most recent songs by selecting the automatically created playlist named "Recently added"

## Additional features

- **Bluetooth and headset support**: Control the playback via Bluetooth or wired headsets
- **Built-in equalizer**: Fine-tune the audio with customizable settings

## Screenshots

<div>

  <img src="metadata/screenshots/1.jpg" width="30%"/>
  <img src="metadata/screenshots/2.jpg" width="30%"/>
  <img src="metadata/screenshots/3.jpg" width="30%"/>
  <img src="metadata/screenshots/4.jpg" width="30%"/>
  <img src="metadata/screenshots/5.jpg" width="30%"/>
  <img src="metadata/screenshots/6.jpg" width="30%"/>
  <img src="metadata/screenshots/7.jpg" width="30%"/>
  <img src="metadata/screenshots/8.jpg" width="30%"/>
  <img src="metadata/screenshots/9.jpg" width="30%"/>
  <img src="metadata/screenshots/10.jpg" width="30%"/>
  
</div>

---

## Tech stack

- **Android (Kotlin)**
- **Data storage**: Room (SQLite, Local storage)
- **Data source**: MediaStore (Local source)
- **Architecture**: Clean Architecture
- **Build tools**: Gradle

## How to build

### Prerequisites

- Android Studio

### Installation

```bash
    git clone https://github.com/SzaboSandor1016/MusicPlayer.git
    cd MusicPlayer
```

or simply download and install directly on a device using the latest released APK: [Download APK](https://github.com/SzaboSandor1016/MusicPlayer/releases/download/v1.0.1/app-debug.apk)

### Running

- Open the project in Android Studio
- Sync Gradle and run on a device

## Furher improvements

- [ ] Support for organizing music by genres, artists and albums
- [ ] Lyrics display
- [ ] Review and rework of UI components
