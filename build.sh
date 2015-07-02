#!/usr/bin/env sh
sbt compile && rm -f target/android-bin/easy2-debug.apk && sbt android:package-debug && cp target/android-bin/easy2-debug.apk target/android-bin/classes/easy2.apk
