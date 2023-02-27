package com.sibsintent

import com.google.gson.annotations.SerializedName

class MessageToSend {
  @SerializedName("reference")
  private var reference: String? = null

  @SerializedName("amount")
  private var amount: String? = null

  fun setReference(reference: String) {
    this.reference = reference
  }

  fun setAmount(amount: String) {
    this.amount = amount
  }
}
