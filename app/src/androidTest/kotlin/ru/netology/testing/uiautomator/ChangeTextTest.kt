package ru.netology.testing.uiautomator

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    // Правильное объявление device
    private val device: UiDevice by lazy {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    // Тестовые данные
    private companion object {
        const val SETTINGS_PACKAGE = "com.android.settings"
        const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"
        const val TIMEOUT = 8000L
        const val TEST_TEXT = "Test Text"
        const val EMPTY_TEXT = ""
    }

    @Before
    fun setUp() {
        // Инициализация устройства
        device.pressHome()
        device.wait(Until.hasObject(By.pkg(device.launcherPackageName)), TIMEOUT)
    }

    private fun launchApp(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: throw IllegalStateException("App $packageName not found")

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Test
    fun testChangeText() {
        launchApp(MODEL_PACKAGE)

        // Ввод текста
        device.findObject(By.res(MODEL_PACKAGE, "userInput")).apply {
            text = TEST_TEXT
        }

        // Нажатие кнопки
        device.findObject(By.res(MODEL_PACKAGE, "buttonChange")).click()

        // Проверка результата
        val result = device.findObject(By.res(MODEL_PACKAGE, "textToBeChanged")).text
        assertEquals(TEST_TEXT, result)
    }

    @Test
    fun testEmptyInput() {
        launchApp(MODEL_PACKAGE)

        val originalText = device.findObject(By.res(MODEL_PACKAGE, "textToBeChanged")).text

        // Попытка ввода пустого текста
        device.findObject(By.res(MODEL_PACKAGE, "userInput")).apply {
            text = EMPTY_TEXT
        }
        device.findObject(By.res(MODEL_PACKAGE, "buttonChange")).click()

        val result = device.findObject(By.res(MODEL_PACKAGE, "textToBeChanged")).text
        assertEquals(originalText, result)
    }

    @Test
    fun testActivityNavigation() {
        launchApp(MODEL_PACKAGE)

        device.findObject(By.res(MODEL_PACKAGE, "userInput")).apply {
            text = TEST_TEXT
        }
        device.findObject(By.res(MODEL_PACKAGE, "buttonActivity")).click()

        val result = device.wait(
            Until.findObject(By.res(MODEL_PACKAGE, "text")),
            TIMEOUT
        )?.text
        assertEquals(TEST_TEXT, result)
    }
}