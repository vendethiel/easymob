package com.etna.easymob

import java.{util => ju}

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.estimote.sdk.BeaconManager.{ServiceReadyCallback, RangingListener}
import com.estimote.sdk.{BeaconManager, Beacon, Region}

import scala.collection.mutable

class HomeActivity extends Activity with TypedFindView {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.home)

    for (user <- User.instance) {
      findView(TR.welcometext).setText(s"hey ${user.username}@${user.id}")

      val beaconManager = new BeaconManager(this)
      val connectedBeacons = mutable.HashMap[String, Long]()

      if (!beaconManager.hasBluetooth) {
        Toast.makeText(this, "This device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show()
        return
      }

      beaconManager.setRangingListener(new RangingListener {
        override def onBeaconsDiscovered(region: Region, beacons: ju.List[Beacon]): Unit = {
          import scala.collection.JavaConverters._
          import BeaconAugmenter.SuperBeacon

          var seenBeacons = mutable.Seq[String]()
          for (beacon <- beacons.asScala) {
            if (connectedBeacons.contains(beacon.key)) {
              // mark this beacon as "still connected"
              seenBeacons :+= beacon.key
            } else {
              connectedBeacons(beacon.key) = System.currentTimeMillis()
            }
          }

          val disconnectedBeacons =
            for {(beaconKey, time) <- connectedBeacons
                 if !seenBeacons.contains(beaconKey)
            } yield {
              (beaconKey, System.currentTimeMillis() - time)
            }
        }
      })

      beaconManager.connect(new ServiceReadyCallback {
        override def onServiceReady(): Unit = beaconManager.startRanging(MainActivity.ALL_ESTIMOTE_BEACONS)
      })
    }
  }
}
