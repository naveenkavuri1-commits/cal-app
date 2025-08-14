# Keep attributes for annotations
-keepattributes *Annotation*

# Keep application classes (adjust as needed)
-keep class com.example.compkncalculator.** { *; }
-dontwarn com.google.errorprone.annotations.**
-dontwarn org.checkerframework.**