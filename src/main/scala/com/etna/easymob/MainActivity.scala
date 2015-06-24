package com.etna.easymob

import android.app.Activity
import android.os.Bundle
//import com.es

class MainActivity extends Activity with TypedFindView {
  /** Called when the activity is first created. */
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

  }
}
