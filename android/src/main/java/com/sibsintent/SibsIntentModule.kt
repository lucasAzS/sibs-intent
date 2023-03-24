package com.sibsintent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.facebook.react.bridge.*
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule
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
  ReactContextBaseJavaModule(reactContext), ActivityEventListener {
  companion object {
    const val NAME = "SibsIntent"

    private const val REQUEST_CODE = 1001
  }

  private val activityRequestCode: Int = 1001
  private val EVENT_NAME = "onIntentResponse"

  private var promise: Promise? = null

  init {
    reactContext.addActivityEventListener(this)
  }

  override fun getName(): String {
    return NAME
  }

  /**
   * The function is called when the activity is finished and returns a result
   *
   * @param activity The current activity
   * @param requestCode This is the request code that you passed to the startActivityForResult() method.
   * @param resultCode This is the result code returned by the activity.
   * @param data Intent?
   */
  override fun onActivityResult(
    activity: Activity?,
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
  ) {
    Log.d("ACTIVITY", activity.toString())
    Log.d("REQUEST_CODE", requestCode.toString())
    Log.d("RESULT_CODE", resultCode.toString())
    Log.d("DATA", data?.toString().toString())

    if (requestCode == REQUEST_CODE) {
      var status = ""
      var errorCode = ""
      var date = ""
      var reference = ""
      var amount = ""

      if (resultCode == Activity.RESULT_OK && data != null) {

        Log.d("error", "CALL_IN_ERROR")
        Log.d("status", "CALL_IN_STATUS")
        amount = data.getStringExtra(CALLIN_AMOUNT_KEY) ?: ""
        date = data.getStringExtra(CALLIN_DATE_KEY) ?: ""
        reference = data.getStringExtra(CALLIN_REF) ?: ""
        status = data.getStringExtra(CALLIN_ERROR_KEY) ?: ""
        errorCode = data.getStringExtra(CALLIN_STATUS_KEY) ?: ""


        val jsonObject = Arguments.createMap()

        // Set key-value pairs in the JSON object
        jsonObject.putString("Status", status)
        jsonObject.putString("ErrorCode", errorCode)

        // Use the event emitter to send the response to JavaScript
        reactApplicationContext
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
          .emit(EVENT_NAME, jsonObject)

        Toast.makeText(
          reactApplicationContext,
          "STATUS2: $status\nError: $errorCode\nAmount: $amount\nDate: $date\nReference: $reference",
          Toast.LENGTH_LONG,
        ).show()
        promise?.resolve(true)
      } else {
        promise?.resolve(false)
      }
    }
  }

  // is needed to implement the onNewIntent() method because of the ActivityEventListener

  override fun onNewIntent(intent: Intent?) {}

  @ReactMethod
  fun openIntent(
    packageId: String,
    className: String,
    value: String,
    reference: String,
    promise: Promise,
  ) {
    try {
      val launchIntent = Intent()
      launchIntent.setClassName(
        packageId,
        className,
      )
      val messageToSend = MessageToSend()
      messageToSend.setReference(reference)
      val ammount = value.replace("[^\\d.]".toRegex(), "")
      messageToSend.setAmmount(ammount)
      val gson = GsonBuilder().create()
      val message = gson.toJson(messageToSend, MessageToSend::class.java)
      val bytes = message.toByteArray(Charsets.UTF_8)
      val base64msg = Base64.encodeToString(bytes, Base64.DEFAULT).trim()

      Log.d("Debug", "Before condition: base64msg = $base64msg")
      if (base64msg == "eyJhbW1vdW50IjoiIiwicmVmZXJlbmNlIjoiYXNkIn0=") {
        Log.d("Debug", "Inside condition")
        promise.resolve(false)
        return
      }
      Log.d("Debug", "After condition")

      val data = Bundle()
      data.putString(PACKAGE_ID, "com.sibsintentexample")
      data.putBoolean(REQUEST_RESPONSE, true)
      data.putString(BASE64REFERENCE, base64msg)
      data.putInt(REQUEST_KEY, REQUEST_CODE)
      launchIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
      launchIntent.putExtra(DATA_MPOS, data)
      Log.d("data", data.toString())

      if (currentActivity != null) {
        Log.d("SibsIntentModule", "Starting activity for result")
        currentActivity?.startActivityForResult(launchIntent, REQUEST_CODE)
      } else {
        Log.d("SibsIntentModule", "Current activity is null")
      }

      promise.resolve(true)
    } catch (e: Exception) {
      Log.e("error", e.message.toString())
      promise.reject(e.message, "Package not found")
    }
  }
}
