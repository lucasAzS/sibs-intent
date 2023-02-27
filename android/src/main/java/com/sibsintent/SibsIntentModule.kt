package com.sibsintent

import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
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
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun openIntent(packageId: String, value: String, reference: String, promise: Promise) {
    val packageManager: PackageManager = reactApplicationContext.packageManager

    try {
      val launchIntent: Intent? = packageManager.getLaunchIntentForPackage(packageId)

      val messageToSend = MessageToSend()
      messageToSend.setReference(reference)

      val amount = value.replace("[^\\d.]".toRegex(), "")
      messageToSend.setAmount(amount)

      val gson = GsonBuilder().create()
      val message = gson.toJson(messageToSend, MessageToSend::class.java)

      val bytes = message.toByteArray(Charsets.UTF_8)
      val base64msg = Base64.encodeToString(bytes, Base64.DEFAULT)


      val data =  Bundle();
      data.putString(PACKAGE_ID, "com.sibsintentexample");
      data.putBoolean(REQUEST_RESPONSE, true);
      data.putString(BASE64REFERENCE,base64msg);
      data.putInt(REQUEST_KEY, activityRequestCode);
      // launchIntent?.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP;
      launchIntent?.putExtra(DATA_MPOS, data);

      Log.d("data", data.toString());

      reactApplicationContext.startActivity(launchIntent)


      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject(e.message, "Package not found")
    }
  }}

//  @ReactMethod
//   fun createPendingIntent(reference: String, value: String) {
//    val packageManager: PackageManager = reactApplicationContext.packageManager
//    val launchIntent: Intent? = packageManager.getLaunchIntentForPackage(packageId)
//
//    // Package of Smartpos that will be called
//
//    val stackBuilder = TaskStackBuilder.create(reactApplicationContext)
//    stackBuilder.addNextIntent(launchIntent)
//    // create a json with value and reference
//    val messageToSend = MessageToSend()
//    // This is the value in cents, for example 1000 = 10.00€
//    val amount = value.replace("[^\\d.]".toRegex(), "")
//    messageToSend.setAmount(amount)
//
//    // This is the reference we will pass for mPOS (String up to 50 characters)
//    messageToSend.setReference(reference)
//
//    // Convert the MessageToSend object to Json using Gson
//    val gson = GsonBuilder().create()
//    val message = gson.toJson(messageToSend, MessageToSend::class.java)
//    // Convert json to a Base64
//    val bytes = message.toByteArray(Charsets.UTF_8)
//    val base64msg = Base64.encodeToString(bytes, Base64.DEFAULT)
//    // Create a bundle and add it to Intent to call mpos and send data over
//    val data = Bundle().apply {
//      // Package of the application that is calling mPOS
//      putString(PACKAGE_ID,"com.sibsintent")
//      // Flag to tell mPOS if this app requires a response
//      putBoolean(REQUEST_RESPONSE,false)
//      putBoolean("CALL_IN_APP_FECHO", false)
//      // Message with amount and reference
//      putString(BASE64REFERENCE, base64msg)
//      // Activity request code
//      putInt(REQUEST_KEY, activityRequestCode)
//    }
//    launchIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//    launchIntent.putExtra(DATA_MPOS, data)
//    reactApplicationContext.startActivity(launchIntent)
//  }
//}

//  private fun onNewIntent(intent: Intent?) {
//    var status: String? = ""
//    var errorCode: String? = ""
//    // get response from mpos
//    if (intent != null && intent.extras != null) {
//      errorCode = intent.extras!!.getString(Constants.CALLIN_ERROR_KEY)
//      status = intent.extras!!.getString(Constants.CALLIN_STATUS_KEY)
//    }
//    Toast.makeText(applicationContext, "STATUS1: $status Error $errorCode", Toast.LENGTH_SHORT)
//      .show()
//    onNewIntent(intent)
//  }
//
//}
