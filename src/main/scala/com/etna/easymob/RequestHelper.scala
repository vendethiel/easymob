package com.etna.easymob

import com.etna.easymob.{User => EasyUser}
import org.json.JSONObject
import scalaz._
import Scalaz._

object RequestHelper {
  val host = "http://estitracker.esy.es/index.php"

  object User {
    def authenticate(email: String, password: String): \/[String, Int] =
      getJSONFromURL(s"$host/api/connect?mail=$email&password=$password").flatMap(json => {
        if (json.has("idUser") && json.getInt("idUser") != 0)
          json.getInt("idUser").right
        else
          "No user was found".left
      })

    def register(email: String, password: String): \/[String, Int] =
      getJSONFromURL(s"$host/api/inscription?mail=$email&password=$password").flatMap(json => {
        if (json.has("idUser"))
          json.getInt("idUser").right
        else if (json.has("error"))
          json.getString("error").left
        else
          "Incomplete response from server".left
      })

  }

  object TimeMgr {
    def getState(user: EasyUser, beaconKey: String): \/[String, String] =
      getJSONFromURL(s"$host/api/check?beacon_key=$beaconKey&idUser=${user.id}").flatMap(json => {
        if (json.has("state")) json.getString("state").right
        else "No state provided by server".left
      })

    def sendTimestamp(user: EasyUser, beaconKey: String, timeDiff: Long): \/[String, Boolean] =
      getJSONFromURL(s"$host/api/addtime?beacon_key=$beaconKey&idUser=${user.id}&time=$timeDiff").flatMap(json => {
        if (json.has("success")) json.getBoolean("success").right
        else "Unable to reach server".left
      })
  }

  object Beacons {
    def nameList(): \/[String, Map[String, String]] =
      getJSONFromURL(s"$host/api/list").map(json => {
        import scala.collection.JavaConverters._
        (for (key <- json.keys().asScala)
          yield (key, json.getString(key))
        ).toMap
      })
  }

  def fromURL(url: String): \/[String, String] = try {
    println(s"Querying $url")
    val requester = new HttpRequestHelper()
    requester.fetchData(url).right
  } catch {
    case e: Exception =>
      e.printStackTrace()
      "Unable to reach server".left
  }

  def parseJSON(jsonString: String): \/[String, JSONObject] = try {
    new JSONObject(jsonString).right
  } catch {
    case e: Exception =>
      println(s"Error parsing JSON: $jsonString")
      e.printStackTrace()
      "Bogus answer from answer".left
  }

  def getJSONFromURL(url: String): \/[String, JSONObject] =
    fromURL(url).flatMap(parseJSON)

}


