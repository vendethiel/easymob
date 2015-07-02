package com.etna.easymob

import java.{util => ju}

import android.app.Activity
import android.content.Intent
import android.os.{StrictMode, Bundle}
import android.view.View
import android.widget.Toast
import com.estimote.sdk.BeaconManager.{NearableListener, ServiceReadyCallback, RangingListener}
import com.estimote.sdk.{Nearable, Region, Beacon, BeaconManager}
import org.json.{JSONObject, JSONException}

import scala.collection.mutable

class MainActivity extends Activity with TypedFindView { self =>
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.main)

    val emailField = findView(TR.email)
    val passwordField = findView(TR.password)
    findView(TR.submit).setOnClickListener(new View.OnClickListener {
      override def onClick(view: View): Unit = {
        val email = emailField.getText.toString
        val password = passwordField.getText.toString

        println(s"I HOPE YOU HAVE $email AND $password")

        (for (userId <- RequestHelper.User.authenticate(email, password)) yield {
          User.instance = Some(User(userId, email))

          val intent = new Intent(self, classOf[HomeActivity])
          startActivity(intent)
        }) getOrElse {
          Toast.makeText(self, "User does not exist", Toast.LENGTH_LONG).show()
        }
      }
    })

    hackThreadPolicy()
  }

  override def onStop(): Unit = {
    //finish()
    super.onStop()
  }

  private def hackThreadPolicy(): Unit = {
    val policy = new StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
  }
}
