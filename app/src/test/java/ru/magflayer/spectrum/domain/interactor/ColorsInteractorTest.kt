package ru.magflayer.spectrum.domain.interactor

import android.content.Context
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import ru.magflayer.spectrum.data.android.ResourceManager
import ru.magflayer.spectrum.data.database.ColorInfoRepositoryTest

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
    fun colorName_available() {
        val result = colorInfoInteractor?.findColorNameByHex("#FF2400")

        result?.test()
                ?.assertValue("Алый")
                ?.assertCompleted()
                ?.assertNoErrors()
    }

    @Test
    fun colorName_error() {
        val errorResult = colorInfoInteractor?.findColorNameByHex("")

        errorResult?.test()
                ?.assertError(IndexOutOfBoundsException::class.java)
    }

    @Test
    fun ncsColor_available() {
        val result = colorInfoInteractor?.findNcsColorByHex("#D8D8D7")

        result?.test()
                ?.assertValue("NCS S 2000-N")
                ?.assertCompleted()
                ?.assertNoErrors()
    }

    @Test
    fun ncsColor_error() {
        val errorResult = colorInfoInteractor?.findNcsColorByHex("")

        errorResult?.test()
                ?.assertError(IndexOutOfBoundsException::class.java)
    }

}
