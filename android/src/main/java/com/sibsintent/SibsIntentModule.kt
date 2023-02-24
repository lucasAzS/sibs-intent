package com.sibsintent

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise

class SibsIntentModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  companion object {
    const val NAME = "SibsIntent"
  }
  @ReactMethod
  fun openIntent(packageId: String, promise: Promise) {
    val packageManager: PackageManager = reactApplicationContext.packageManager
    try {
      val launchIntent: Intent? = packageManager.getLaunchIntentForPackage(packageId)
       // val launchIntent: Intent? = packageManager.getLaunchIntentForPackage("com.google.android.youtube")
      reactApplicationContext.startActivity(launchIntent)
      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject(e.message, "Package not found")
    }
  }

}
