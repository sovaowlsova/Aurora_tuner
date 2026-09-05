# 🎵 Aurora Tuner 
An in-development musical tuner app for Android. The app features different instruments and various tunings for each of them, including built-in and user-generated ones

<img width="1172" height="1151" alt="showcase" src="https://github.com/user-attachments/assets/d0c27e4a-d70b-45ae-9076-5b1507b9572d" />

## 🎯 Features
### Implemented features:
* ✅ Instrument selection
* ✅ Note recognition using the YIN algorithm
* ✅ Tuning selection
* ✅ News section with refresh feature and error handling
* ✅ News are fetched from a GitHub pages website (https://sovaowlsova.github.io/auroratuner-github.io/)
* ✅ Guitar and ukulele pictures drawn in Figma
* ✅  Automatic instrument fragment creation for user-generated instruments
* ✅ Russian language
### Planned features:
* 🚧 Tuning editor which allows users to create and share their own instruments and tunings
* 🚧 JSON file structure for user-generated tunings
* 🚧 Settings section with an option to import and manage user-generated tunings
* 🚧 More languages, including: Ukrainian, Belarusian, German, Spanish and Czech
* 🚧 Cents system for detecting a tuned note instead of the current frequency-based flat value one
* 🚧 Night and day modes using styles
### Known bugs, imperfections and incomplete features:
* 👀 Layout doesn't use styles yet. Only predetermined color values
* 👀 Settings button is it's own object and is not attached to the toolbar
* 👀 App layout is not consistent through the different screen sizes
## 🛠️ Tech stack
* Android
* Java 17
* Gradle (Kotlin)
* Apache Maths for FFT (Faster Fourier Transform)
* Jackson for JSON files handling
* Minimal Android API level 26
## 🔪 Killer feature
The app is meant to be a tuner for all musical instruments. This is made possible by allowing the users to create, share and import their own instruments and tunings for those instruments
## ©️ Copyright
All rights reserved.
© 2026 Artyom Safin. Email: temkasafin@gmail.com
