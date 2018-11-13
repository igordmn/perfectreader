package com.dmi.util.io

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.internal.SerialClassDescImpl

object ByteArraySerializer: KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("kotlin.ByteArray")

    override fun serialize(output: Encoder, obj: ByteArray) {
        output.encodeInt(obj.size)
        obj.forEach {
            output.encodeByte(it)
        }
    }

    override fun deserialize(input: Decoder): ByteArray {
        val size = input.decodeInt()
        val array = ByteArray(size)
        var i = 0
        repeat(size) {
            array[i++] = input.decodeByte()
        }
        return array
    }
}