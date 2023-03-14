
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
  companion object {
    const val NAME = "SibsIntent"
  }
  private val activityRequestCode: Int = 100

//  private val sw: SwitchCompat? = null
  override fun getName(): String {
    return NAME
  }

// Example method
// See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun openIntent(
    packageId: String,
    className: String,
    value: String,
    reference: String,
    promise: Promise,
  ) {
    val packageManager: PackageManager = reactApplicationContext.packageManager
    try {
      val launchIntent = Intent()
      launchIntent.setClassName(
        packageId,
        className,
      )
      val messageToSend = MessageToSend()
      messageToSend.setReference(reference)
      val ammount = value.replace("[\\d.]".toRegex(), "")
      messageToSend.setAmmount(ammount)
      val gson = GsonBuilder().create()
      val message = gson.toJson(messageToSend, MessageToSend::class.java)
      val bytes = message.toByteArray(Charsets.UTF_8)
      val base64msg = Base64.encodeToString(bytes, Base64.DEFAULT)
      val data = Bundle()
      data.putString(PACKAGE_ID, "com.sibsintentexample")
      data.putBoolean(REQUEST_RESPONSE, true)
      data.putString(BASE64REFERENCE, base64msg)
      data.putInt(REQUEST_KEY, activityRequestCode)
      launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      launchIntent.putExtra(DATA_MPOS, data)
      Log.d("data", data.toString())
      reactApplicationContext.startActivity(launchIntent)
      promise.resolve(true)
    } catch (e: Exception) {
      Log.e("error", e.message.toString())
      promise.reject(e.message, "Package not found")
    }
  }

  @ReactMethod
  fun onNewIntent(intent: Intent?) {
    var status = ""
    var errorCode = ""
    var date = ""
    var reference = ""
    var amount = ""
    intent?.extras?.run {
      errorCode = getString(CALLIN_ERROR_KEY) ?: ""
      status = getString(CALLIN_STATUS_KEY) ?: ""
      date = getString(CALLIN_DATE_KEY) ?: ""
      amount = getString(CALLIN_AMOUNT_KEY) ?: ""
      reference = getString(CALLIN_REF) ?: ""
    }
    Toast.makeText(
      reactApplicationContext,
      "STATUS2: $status\nError: $errorCode\nAmount: $amount\nDate: $date\nReference: $reference",
      Toast.LENGTH_LONG,
    ).show()
    onNewIntent(intent)
  }
}

