# MusicPlayer

A simple music player built for offline listening.
In addition to simply playing a music, users can create custom play queues featuring their favorite songs, 
manage custom and automatically created playlists like "Recently added" or "Favorites", quickly find songs by their artist, genre or album
using one of the views dedicated to these use cases.

---

## Features

- **Music player**

  - Assemble custom play queues of songs even from multiple different playlists (Add, remove and reorder songs manually)
  - Modify the play queue to make (multiple) selected songs played right after the current one
  - Shuffle the order or set the repeat mode of playback
  - Add a song to "Favorites" directly from the player itself

- **Playlists and "All songs"**

  - Quickly access all the songs by selecting the "All songs" option on the main screen (Synchronized with the device's storage automatically)
  - Create and manage custom playlists
  - See the most recent songs by selecting the automatically created playlist named "Recently added"

- **Genres, artists and albums**

  - **Artists**: Get a quick overview of songs grouped by artist, and directly access the albums containing them
  - **Albums**: Get a glimpse of albums to be found on your device
  - **Genres**: Take a look on songs with particular genre

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
  
</div>

---

## Tech stack

- **Android (Kotlin)**
- **Data storage**: Room (SQLite, Local storage)
- **Data source**: MediaStore (Local source)
- **Architecture**: Clean Architecture with MVVM
- **Build tools**: Gradle

## How to build

### Prerequisites

- Android Studio

### Installation

```bash
    git clone https://github.com/SzaboSandor1016/MusicPlayer.git
    cd MusicPlayer
```

or simply download and install directly on a device using the latest released APK: [Download APK](https://github.com/SzaboSandor1016/MusicPlayer/releases/download/v1.1.0/MusicPlayer_v1.1.0.apk)

### Running

- Open the project in Android Studio
- Sync Gradle and run on a device

## Furher improvements

- [ ] Lyrics display
- [ ] Review and rework of UI components
