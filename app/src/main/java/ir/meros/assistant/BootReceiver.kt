package ir.meros.assistant// BootReceiver.kt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_USER_PRESENT -> {
                Log.d("BootReceiver", "Device unlocked or boot completed")
                val serviceIntent = Intent(context, BackgroundService::class.java)
                context.startForegroundService(serviceIntent)
            }
        }
    }
}