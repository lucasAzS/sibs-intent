package com.sibsintent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
   * Handles activity result for the SibsIntentModule native module.
   *
   * This function processes the activity result received from an external
   * activity and sends a JSON object containing the result information to
   * the JavaScript side using the event emitter.
   *
   * @param activity The activity that initiated the result.
   * @param requestCode The integer request code originally supplied to
   *     startActivityForResult().
   * @param resultCode The integer result code returned by the child activity
   *     through its setResult().
   * @param data An Intent, which can return result data to the caller
   *     (various data can be attached as "extras").
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

    val jsonObject = Arguments.createMap()
    jsonObject.putString("resultCode", resultCode.toString())


    if (requestCode == REQUEST_CODE) {

      if (resultCode == Activity.RESULT_OK && data != null) {

        val amount = data.getStringExtra(CALLIN_AMOUNT_KEY) ?: ""
        val date = data.getStringExtra(CALLIN_DATE_KEY) ?: ""
        val reference = data.getStringExtra(CALLIN_REF) ?: ""
        val status = data.getStringExtra(CALLIN_STATUS_KEY) ?: ""
        val errorCode = data.getStringExtra(CALLIN_ERROR_KEY) ?: ""


        // Set key-value pairs in the JSON object
        jsonObject.putString("status", status)
        jsonObject.putString("errorCode", errorCode)
        jsonObject.putString("date", date)
        jsonObject.putString("reference", reference)
        jsonObject.putString("amount", amount)
        Log.d("jsonObject", jsonObject.toString())

        // Use the event emitter to send the response to JavaScript
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
          .emit(EVENT_NAME, jsonObject)

        promise?.resolve(true)
      } else {
        promise?.resolve(false)
      }
    }
  }

  // is needed to implement the onNewIntent() method because of the ActivityEventListener

  override fun onNewIntent(intent: Intent?) {}

  /**
   * Opens an external activity using an intent with an encoded message.
   *
   * This function launches an external activity from another application
   * using an intent. It also passes a base64 encoded message to the external
   * activity as an extra in the intent.
   *
   * @param packageId The package identifier of the external application to
   *     launch.
   * @param className The class name of the external activity to be launched.
   * @param value The value to be included in the encoded message.
   * @param reference The reference to be included in the encoded message.
   * @param promise A Promise instance to handle the success or failure of
   *     the operation.
   */
  @ReactMethod
  fun startActivityWithIntentMessage(
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

//      Log.d("Debug", "Before condition: base64msg = $base64msg")
//      if (base64msg == "eyJhbW1vdW50IjoiIiwicmVmZXJlbmNlIjoiYXNkIn0=") {
//        Log.d("Debug", "Inside condition")
//        promise.resolve(false)
//        return
//      }
//      Log.d("Debug", "After condition")

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

  @ReactMethod
  fun addListener(type: String?) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  fun removeListeners(type: Int?) {
    // Keep: Required for RN built in Event Emitter Calls.
  }
}
