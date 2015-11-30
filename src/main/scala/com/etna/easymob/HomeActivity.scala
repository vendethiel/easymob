package com.etna.easymob

import java.{util => ju}

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.estimote.sdk.BeaconManager.{ServiceReadyCallback, RangingListener}
import com.estimote.sdk.Utils.Proximity
import com.estimote.sdk.{BeaconManager, Beacon, Region, Utils => EstiUtils}

import scala.collection.mutable
import scalaz._
import Scalaz._

object BeaconAugmenter {
  implicit class SuperBeacon(beacon: Beacon) extends AnyRef {
    val key = s"${beacon.getMajor}_${beacon.getMinor}"

    val proximity = EstiUtils.computeProximity(beacon)
  }
}

object HomeActivity {
  val ESTIMOTE_PROXIMITY_UUID = "b9407f30-f5f8-466e-aff9-25556b57fe6d"
  val ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null)
}

class HomeActivity extends Activity with TypedFindView { view =>
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.home)

    for (user <- User.instance) {
      val welcomeMsg = s"Bienvenue, ${user.username} (#${user.id})"
      findView(TR.welcometext).setText(welcomeMsg)

      val beaconNames = RequestHelper.Beacons.nameList() | Map()

      val beaconManager = new BeaconManager(this)
      val connectedBeacons = mutable.HashMap[String, Long]()

      if (!beaconManager.hasBluetooth) {
        Toast.makeText(this, "This device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show()
        return
      }

      val listener = new RangingListener {
        private def sendTimestamp(user: User, beaconKey: String, timeDiff: Long): Unit = {
          RequestHelper.TimeMgr.sendTimestamp(user, beaconKey, timeDiff) match {
            case \/-(true) =>
              Toast.makeText(view, "Your time online was added to the server!", Toast.LENGTH_LONG).show()
            case \/-(_) =>
              Toast.makeText(view, "Unable to add your time to that beacon...", Toast.LENGTH_LONG).show()
            case -\/(error) =>
              Toast.makeText(view, error, Toast.LENGTH_LONG).show()
          }
        }

        override def onBeaconsDiscovered(region: Region, unfilteredBeacons: ju.List[Beacon]): Unit = {
          import scala.collection.JavaConverters._
          import BeaconAugmenter.SuperBeacon

          val beacons = unfilteredBeacons.asScala.filter(_.proximity == Proximity.IMMEDIATE)

          println(beacons)
          println(connectedBeacons)
          var seenBeacons = mutable.Seq[String]()
          for (beacon <- beacons) {
            seenBeacons :+= beacon.key
            if (!connectedBeacons.contains(beacon.key)) {
              connectedBeacons(beacon.key) = System.currentTimeMillis()
            }
          }

          val disconnectedBeacons =
            for {(beaconKey, time) <- connectedBeacons
                 if !seenBeacons.contains(beaconKey)
            } yield {
              connectedBeacons.remove(beaconKey)
              val timeDiff = System.currentTimeMillis() - time

              for (state <- RequestHelper.TimeMgr.getState(user, beaconKey)) {
                state match {
                  case "timestamp" => sendTimestamp(user, beaconKey, timeDiff)
                  case "ok" | "" =>
                  case link: String =>
                    val browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(browserIntent)
                }
              }
            }


          println("connected" + connectedBeacons)
          println("seen" + seenBeacons)
          println("disco" + disconnectedBeacons)
          println("------------------------------------------------")
          println("------------------------------------------------")

          val beaconsString = connectedBeacons.keys.map(s => beaconNames.getOrElse(s, s)).mkString("\n")
          findView(TR.welcometext).setText(s"$welcomeMsg\nbeacons\n$beaconsString")
        }
      }
      beaconManager.setRangingListener(listener)

      // 10 seconds
      beaconManager.setForegroundScanPeriod(10000, 10000)
      beaconManager.setBackgroundScanPeriod(10000, 10000)

      beaconManager.connect(new ServiceReadyCallback {
        override def onServiceReady(): Unit = beaconManager.startRanging(HomeActivity.ALL_ESTIMOTE_BEACONS)
      })
    }
  }
}
