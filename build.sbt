import android.Dependencies.aar

android.Plugin.androidBuild

localProjects in Android := Nil

minSdkVersion in Android := "android-21"

targetSdkVersion in Android := "android-21"

platformTarget in Android := "android-21"

localAars in Android += baseDirectory.value / "libs" / "estimote-sdk-preview.aar"

libraryDependencies ++= Seq(
  "com.android.support" % "appcompat-v7" % "22.0.0"
)