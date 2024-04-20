# An artificial life simulator

Try a live demo at [https://vonox7.github.io/monkey-sim/](https://vonox7.github.io/monkey-sim/).
You might need a strong computer to run the whole simulation without frame drops.

## Building

This is a [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) project
targeting Web via [Webassembly](https://kotl.in/wasm/) & Desktop.
UI is build with [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform) and
a custom canvas renderer for the simulation itself.

Run the JVM desktop application locally with `./gradlew desktopRun -DmainClass=MainKt --quiet`.
And the web application with `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`.