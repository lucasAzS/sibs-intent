package com.sibsintent



internal object Constants {
  const val SIBS = "SIBS"
  const val BCP = "BCP"
  const val BST = "BST"
  const val DATA_MPOS = "DATA_MPOS" //key with name of bundle
  const val PACKAGE_ID = "PACKAGE_ID" //key with caller package id
  const val BASE64REFERENCE = "BASE64REFERENCE" //key for base64 sent to mpos
  const val REQUEST_RESPONSE =
    "RETURN_VALUE_BOOLEAN" //key with boolean indicating if response is required
  const val REQUEST_KEY = "REQUEST_KEY"
  const val CALLIN_ERROR_KEY = "CALL_IN_ERROR" //key for response field : error
  const val CALLIN_STATUS_KEY = "CALL_IN_STATUS" //key for response field : status
  const val CALLIN_DATE_KEY = "CALLIN_DATE_KEY"
  const val CALLIN_AMOUNT_KEY = "CALLIN_AMOUNT_KEY"
  const val CALLIN_REF = "CALLIN_REF"
}
