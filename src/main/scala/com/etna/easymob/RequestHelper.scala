package com.etna.easymob

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
      getJsonFromURL(s"$host/connection.php?email=$email&password=$password").flatMap(json => {
        val userId = json.getInt("idUser")
        if (userId == 0) None else Some(userId)
      })
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

  def getJsonFromURL(url: String): Option[JSONObject] =
    fromURL(url).flatMap(s => parseJSON(s))
}


