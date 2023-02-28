package com.sibsintent

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.google.gson.GsonBuilder
import com.sibsintent.Constants.BASE64REFERENCE
import com.sibsintent.Constants.CALLIN_AMOUNT_KEY
import com.sibsintent.Constants.CALLIN_DATE_KEY
import com.sibsintent.Constants.CALLIN_ERROR_KEY
import com.sibsintent.Constants.CALLIN_REF
import com.sibsintent.Constants.CALLIN_STATUS_KEY
import com.sibsintent.Constants.DATA_MPOS
import com.sibsintent.Constants.PACKAGE_ID
import com.sibsintent.Constants.REQUEST_KEY
import com.sibsintent.Constants.REQUEST_RESPONSE


class SibsIntentModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private val activityRequestCode: Int = 100


  override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val intent = reactApplicationContext.currentActivity?.intent
    when (intent?.action) {
      Intent.ACTION_SEND -> {
        if ("text/plain" == intent.type) {
          handleSendText(intent) // Handle text being sent
        }

      }
    }
  }

  private fun handleSendText(intent: Intent) {
    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
      Toast.makeText(reactApplicationContext, it, Toast.LENGTH_LONG).show()
    }
  }

  companion object {
    const val NAME = "SibsIntent"
  }


//  private val sw: SwitchCompat? = null


  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun openIntent(reference: String, value: String, packageId: String, promise: Promise) {
    val packageManager: PackageManager = reactApplicationContext.packageManager

    try {
      val launchIntent: Intent? = packageManager.getLaunchIntentForPackage(packageId)

      val messageToSend = MessageToSend()
      val amount = value.replace("[^\\d.]".toRegex(), "")
      messageToSend.setAmount(amount)
      messageToSend.setReference(reference)

      Log.d("amount", amount)
      Log.d("reference", reference)


      val gson = GsonBuilder().create()
      val message = gson.toJson(messageToSend, MessageToSend::class.java)

      val bytes = message.toByteArray(Charsets.UTF_8)
      val base64msg = Base64.encodeToString(bytes, Base64.DEFAULT)


      val data = Bundle()
      data.putString(PACKAGE_ID, "com.sibsintent")
      data.putBoolean(REQUEST_RESPONSE, true)
      data.putString(BASE64REFERENCE, base64msg)
      data.putInt(REQUEST_KEY, activityRequestCode)
//      launchIntent!!.flags = FLAG_ACTIVITY_SINGLE_TOP
      launchIntent!!.putExtra(DATA_MPOS, data)

      Log.d("data", data.toString())

      reactApplicationContext.startActivity(launchIntent)

      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject(e.message, "Package not found")
    }
  }

  @ReactMethod
  @Override
  protected fun onNewIntent(promise: Promise) {
    var status = ""
    var errorCode = ""
    var date = ""
    var reference = ""
    var amount = ""
    // get response from mpos
    try {
      val intent = reactApplicationContext.currentActivity?.intent
      if (intent?.extras != null) {
        Log.d("intent", intent.extras.toString())
        if (intent.extras?.containsKey(CALLIN_ERROR_KEY) == true)
          errorCode = intent.extras!!.getString(CALLIN_ERROR_KEY).toString()
        if (intent.extras!!.containsKey(CALLIN_STATUS_KEY))
          status = intent.extras!!.getString(CALLIN_STATUS_KEY).toString()
        if (intent.extras!!.containsKey(CALLIN_DATE_KEY))
          date = intent.extras!!.getString(CALLIN_DATE_KEY).toString()
        if (intent.extras!!.containsKey(CALLIN_AMOUNT_KEY))
          amount = intent.extras!!.getString(CALLIN_AMOUNT_KEY).toString()
        if (intent.extras!!.containsKey(CALLIN_REF))
          reference = intent.extras!!.getString(CALLIN_REF).toString()

        Toast.makeText(
          reactApplicationContext,
          "Status: $status, Error: $errorCode, Date: $date, Amount: $amount, Reference: $reference",
          Toast.LENGTH_LONG,
        ).show()

        promise.resolve(true)

      } else {
        Toast.makeText(reactApplicationContext, "No data", Toast.LENGTH_LONG).show()
      }
    } catch (e: Exception) {
      promise.reject(e.message, "Error")
    }

  }


}



