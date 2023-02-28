package com.sibsintent

import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.google.gson.GsonBuilder
import com.sibsintent.Constants.BASE64REFERENCE
import com.sibsintent.Constants.DATA_MPOS
import com.sibsintent.Constants.PACKAGE_ID
import com.sibsintent.Constants.REQUEST_KEY
import com.sibsintent.Constants.REQUEST_RESPONSE


class SibsIntentModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private val activityRequestCode: Int = 100


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
      val launchIntent: Intent? =
        packageManager.getLaunchIntentForPackage(packageId)

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
      data.putString(PACKAGE_ID, "com.sibsintentexemple")
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
  fun createPendingIntent(
    reference: String,
    value: String,
    packageId: String,
    promise: Promise,
  ) {
    try {
      val launchIntent = Intent()
      launchIntent.setClassName(packageId, "pt.sibs.android.mpos.activities.SplashScreenActivity")

      val stackBuilder = TaskStackBuilder.create(reactApplicationContext)
      stackBuilder.addNextIntentWithParentStack(launchIntent)

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
      data.putString(PACKAGE_ID, "com.sibsintentexemple")
      data.putBoolean(REQUEST_RESPONSE, true)
      data.putString(BASE64REFERENCE, base64msg)
      data.putInt(REQUEST_KEY, activityRequestCode)
      launchIntent.putExtra(DATA_MPOS, data)

      Log.d("data", data.toString())

      reactApplicationContext.startActivity(launchIntent)

    } catch (e: Exception) {
      promise.reject(e.message, "Package not found")
    }
  }
}


