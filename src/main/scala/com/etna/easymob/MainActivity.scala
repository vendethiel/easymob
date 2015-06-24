package com.etna.easymob

import java.{util => ju}

import android.app.Activity
import android.os.{StrictMode, Bundle}
import com.estimote.sdk.BeaconManager.RangingListener
import com.estimote.sdk.{Region, Beacon, BeaconManager}

class MainActivity extends Activity with TypedFindView {
  val beaconMgr = new BeaconManager(this)
  var tag : String = ""

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    hackThreadPolicy()
    beaconMgr.setRangingListener(new RangingListener {
      override def onBeaconsDiscovered(region: Region, beacons: ju.List[Beacon]): Unit = {
        println(if (beacons.isEmpty) "hey" else ":(")
      }
    })
  }

  override def onStop(): Unit = {
    finish()
  }

  private def hackThreadPolicy(): Unit = {
    val policy = new StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
  }
}
