package com.example.hotelcancelpredict

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFLiteHelper(context: Context) {

    private val interpreter: Interpreter

    private val labelEncoders = mapOf(
        "hotel" to mapOf("Resort Hotel" to 0, "City Hotel" to 1),
        "meal" to mapOf("BB" to 0, "FB" to 1, "HB" to 2, "SC" to 3, "Undefined" to 4),
        "market_segment" to mapOf("Direct" to 0, "Corporate" to 1, "Online TA" to 2, "Offline TA/TO" to 3, "Complementary" to 4, "Groups" to 5, "Undefined" to 6, "Aviation" to 7),
        "distribution_channel" to mapOf("Direct" to 0, "Corporate" to 1, "TA/TO" to 2, "Undefined" to 3, "GDS" to 4),
        "deposit_type" to mapOf("No Deposit" to 0, "Refundable" to 1, "Non Refund" to 2),
        "customer_type" to mapOf("Transient" to 0, "Contract" to 1, "Transient-Party" to 2, "Group" to 3),
        "arrival_date_month" to mapOf("January" to 0, "February" to 1, "March" to 2, "April" to 3, "May" to 4, "June" to 5, "July" to 6, "August" to 7, "September" to 8, "October" to 9, "November" to 10, "December" to 11)
    )

    init {
        val assetFileDescriptor = context.assets.openFd("hotel_cancel_pred.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val fileChannel = inputStream.channel
        val modelBuffer = fileChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, assetFileDescriptor.startOffset, assetFileDescriptor.declaredLength)
        interpreter = Interpreter(modelBuffer)
    }

    fun encode(field: String, value: String): Float {
        return labelEncoders[field]?.get(value)?.toFloat() ?: 0f
    }

    fun predict(inputData: FloatArray): Float {
        val output = Array(1) { FloatArray(1) }
        interpreter.run(arrayOf(inputData), output)
        return output[0][0]
    }
}
