package com.sibsintent

import com.google.gson.annotations.SerializedName

class MessageToSend {
  @SerializedName("reference")
  private var reference: String? = null

  @SerializedName("ammount")
  private var ammount: String? = null

  fun setReference(reference: String) {
    this.reference = reference
  }

  fun setAmmount(ammount: String) {
    this.ammount = ammount
  }
}
