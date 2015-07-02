package com.etna.easymob

import com.estimote.sdk.Beacon
import com.etna.easymob.{User => EasyUser}
import java.io.{IOException, InputStreamReader, BufferedReader}
import java.util.concurrent.TimeUnit

import android.os.AsyncTask
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.json.{JSONObject, JSONException}
import java.io._
import org.apache.http.impl.client.DefaultHttpClient

object RequestHelper {
  val host = "http://esticontroller.esy.es/EstiController/api"

  object User {
    def authenticate(email: String, password: String): Option[Int] =
      getJSONFromURL(s"$host/connection.php?email=$email&password=$password").flatMap(json => {
        val userId = json.getInt("idUser")
        if (userId == 0) None else Some(userId)
      })
  }

  object TimeMgr {
    def getState(user: EasyUser, beaconKey: String): Option[String] = {
      getJSONFromURL(s"$host/findEstiAction.php?estiId=$beaconKey&userId=${user.id}").flatMap(json => {
        if (json.has("state")) Some(json.getString("state"))
        else None
      })
    }

    def sendTimestamp(user: EasyUser, beaconKey: String, timeDiff: Long): Option[Boolean] = {
      getJSONFromURL(s"$host/addTimeConnect.php?estiId=$beaconKey&userId=${user.id}&time=$timeDiff").flatMap(json => {
        if (json.has("success")) Some(json.getBoolean("success"))
        else None
      })
    }
  }

  def fromURL(url: String): Option[String] = try {
    val requester = new HttpRequestHelper()
    Some(requester.fetchData(url))
  } catch {
    case e: Exception =>
      e.printStackTrace()
      None
  }

  def parseJSON(jsonString: String): Option[JSONObject] = try {
    Some(new JSONObject(jsonString))
  } catch {
    case e: Exception =>
      e.printStackTrace()
      None
  }

  def getJSONFromURL(url: String): Option[JSONObject] =
    fromURL(url).flatMap(s => parseJSON(s))
}


