import android.Dependencies.aar

javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7")

android.Plugin.androidBuild

localProjects in Android := Nil

minSdkVersion in Android := "21"

targetSdkVersion in Android := "21"

platformTarget in Android := "android-21"

localAars in Android += baseDirectory.value / "libs" / "estimote-sdk-preview.aar"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.5"
)

dexMulti in Android := true