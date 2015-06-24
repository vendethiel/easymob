import android.Dependencies.aar

android.Plugin.androidBuild

localProjects in Android := Nil

minSdkVersion in Android := "21"

targetSdkVersion in Android := "21"

platformTarget in Android := "android-21"

localAars in Android += baseDirectory.value / "libs" / "estimote-sdk-preview.aar"
