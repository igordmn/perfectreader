package com.dmi.perfectreader.settings

import com.dmi.util.graphic.Color
import com.dmi.util.persist.MemoryValueStore
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value
import kotlinx.serialization.*
import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlin.reflect.KClass

class ThemeSettings(store: ValueStore) {
    private val themeStore = ThemeStore(store)

    var textColor by themeStore.value(Color.BLACK.value)
    var textGammaCorrection by themeStore.value(1F)

    var textShadowEnabled by themeStore.value(false)
    var textShadowAngleDegrees by themeStore.value(0F)
    var textShadowOffsetEm by themeStore.value(0F)
    var textShadowSizeEm by themeStore.value(0.1F)
    var textShadowBlurEm by themeStore.value(0.05F)
    var textShadowColor by themeStore.value(Color.GRAY.withAlpha(128).value)

    var backgroundIsImage by themeStore.value(false)
    var backgroundColor by themeStore.value(Color.WHITE.value)
    var backgroundPath by themeStore.value("assets:///resources/backgrounds/0004.png")
    var backgroundContentAwareResize by themeStore.value(true)

    var selectionColor by store.value(Color(255, 174, 223, 240).value)

    fun save() = themeStore.save()
    fun load(theme: SavedTheme) = themeStore.load(theme)
}

var ThemeSettings.textShadowOpacity: Float
    get() = Color(textShadowColor).alpha / 255F
    set(value) {
        val color = Color(textShadowColor)
        textShadowColor = color.withAlpha((value * 255).toInt()).value
    }

val DefaultStyles = SavedThemes(listOf(
        savedTheme(textColor = Color.BLACK, backgroundColor = Color.WHITE),
        savedTheme(textColor = Color.WHITE, backgroundColor = Color.BLACK)
))

fun savedTheme(textColor: Color, backgroundColor: Color): SavedTheme {
    val store = MemoryValueStore()
    val settings = ThemeSettings(store)
    settings.textColor = textColor.value
    settings.backgroundColor = backgroundColor.value
    return settings.save()
}

fun themeSettings(saved: SavedTheme): ThemeSettings {
    val store = MemoryValueStore()
    val settings = ThemeSettings(store)
    settings.load(saved)
    return settings
}

class ThemeStore(private val original: ValueStore) : ValueStore {
    private val values = ArrayList<ValueStore.Value<Any>>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> value(key: String, default: T, cls: KClass<T>): ValueStore.Value<T> {
        val value = original.value(key, default, cls)
        values.add(value as ValueStore.Value<Any>)
        return value
    }

    fun save(): SavedTheme {
        val list = ArrayList<Any>()
        for (value in values) {
            list.add(value.get())
        }
        return SavedTheme(CBOR.dump(ThemeProperties(list)))
    }

    fun load(theme: SavedTheme) {
        val list = CBOR.load<ThemeProperties>(theme.data).list
        for (i in values.indices) {
            values[i].set(list[i])
        }
    }
}

@Serializable
private class ThemeProperties(val list: ArrayList<Any>)

@Serializable
class SavedTheme(@Serializable(with = ByteArraySerializer::class) val data: ByteArray)

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