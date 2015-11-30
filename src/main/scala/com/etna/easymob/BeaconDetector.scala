package com.etna.easymob

import java.{util => ju}

import com.estimote.sdk.{Beacon, Region, BeaconManager}
import com.estimote.sdk.BeaconManager.{ServiceReadyCallback, RangingListener}

class BeaconDetector(androidContext: android.app.Activity) {
  val beaconManager = new BeaconManager(androidContext)
  val listener = new RangingListener {
    override def onBeaconsDiscovered(region: Region, unfilteredBeacons: ju.List[Beacon]): Unit = {
      println(unfilteredBeacons)
    }
  }
  beaconManager.setRangingListener(listener)

  // 10 seconds
  beaconManager.setForegroundScanPeriod(10000, 10000)
  beaconManager.setBackgroundScanPeriod(10000, 10000)

  def detect(): Unit =
    beaconManager.connect(new ServiceReadyCallback {
      override def onServiceReady(): Unit = beaconManager.startRanging(HomeActivity.ALL_ESTIMOTE_BEACONS)
    })
}
