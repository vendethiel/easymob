package com.etna.easymob

import android.app.Activity
import android.content.Intent
import android.os.{Bundle, StrictMode}
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast

class MainActivity extends Activity with TypedFindView { self =>
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    val emailField = findView(TR.email)
    val passwordField = findView(TR.password)
    findView(TR.login).setOnClickListener(new View.OnClickListener {
      override def onClick(view: View): Unit = {
        val email = emailField.getText.toString
        val password = passwordField.getText.toString

        println(s"I HOPE YOU HAVE $email AND $password")
        RequestHelper.User.authenticate(email, password)
          .leftMap(Toast.makeText(self, _, Toast.LENGTH_LONG).show())
          .foreach(enterWithUser(_, email))
      }
    })

    findView(TR.register).setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val email = emailField.getText.toString
        val password = passwordField.getText.toString

        println(s"GOING FOR $email AND $password")
        RequestHelper.User.register(email, password)
          .leftMap(Toast.makeText(self, _, Toast.LENGTH_LONG).show())
          .foreach(enterWithUser(_, email))
      }
    })

    hackThreadPolicy()
  }

  private def hackThreadPolicy(): Unit = {
    val policy = new StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
  }

  private def enterWithUser(userId: Int, email: String): Unit = {
    User.instance = Some(User(userId, email))

    val intent = new Intent(self, classOf[HomeActivity])
    startActivity(intent)
  }
}
