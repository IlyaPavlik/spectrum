package ru.magflayer.spectrum.data.android

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceManager @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val resources = context.resources

    fun getString(@StringRes id: Int): String {
        return resources.getString(id)
    }

    fun getQuantityString(@PluralsRes id: Int, quantity: Int): String {
        return resources.getQuantityString(id, quantity)
    }

    @Throws(IOException::class)
    fun getAsset(assetName: String): InputStream {
        return resources.assets.open(assetName)
    }
}
