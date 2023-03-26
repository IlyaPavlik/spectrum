package ru.magflayer.spectrum.domain.interactor

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import ru.magflayer.spectrum.data.android.ResourceManager
import ru.magflayer.spectrum.data.database.ColorInfoRepositoryTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ColorsInteractorTest {

    @Mock
    lateinit var context: Context

    private var colorInfoInteractor: ColorInfoInteractor? = null

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        val resourceManager = ResourceManager(context)
        val colorInfoRepository = ColorInfoRepositoryTest()

        colorInfoInteractor = ColorInfoInteractor(colorInfoRepository, resourceManager, Gson())
    }

    @Test
    fun colorName_available() = runTest {
        val result = colorInfoInteractor?.findColorNameByHex("#FF2400")

        assertEquals("Алый", result)
    }

    @Test
    fun colorName_error() = runTest {
        assertFailsWith<IndexOutOfBoundsException> {
            colorInfoInteractor?.findColorNameByHex("")
        }
    }

    @Test
    fun ncsColor_available() = runTest {
        val result = colorInfoInteractor?.findNcsColorByHex("#D8D8D7")

        assertEquals("NCS S 2000-N", result)
    }

    @Test
    fun ncsColor_error() = runTest {
        assertFailsWith<IndexOutOfBoundsException> {
            colorInfoInteractor?.findNcsColorByHex("")
        }
    }
}
