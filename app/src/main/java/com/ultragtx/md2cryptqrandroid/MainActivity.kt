package com.ultragtx.md2cryptqrandroid

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    // Define the necessary permissions
    private val REQUEST_CAMERA_CODE = 1
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val TAG = "YourTag"

//    override fun onPause() {
//        super.onPause()
//        if (!isChangingConfigurations) {
//            finishAndRemoveTask()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set FLAG_SECURE to prevent screenshots
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

//        WebView.setWebContentsDebuggingEnabled(true) // Debugging only

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check and Ask for Permission
        if (!allPermissionsGranted()) {
            Log.d(TAG, "not allPermissionsGranted");
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CAMERA_CODE)
        }
        else {
            Log.d(TAG, "allPermissionsGranted");
            showWebPage()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d(TAG, "onRequestPermissionsResult");

        if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now load your WebView
                showWebPage()
            } else {
                // Permission denied, handle accordingly
                showPermissionDeniedAlert()
            }
        }
    }

    private fun showPermissionDeniedAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Camera Permission Required")
        builder.setMessage("This app requires camera permission to function properly. Please grant the permission in the app settings.")
        builder.setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
            // Close the app or take appropriate action
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun showWebPage() {
        // Get WebView from layout
        val webView: WebView = findViewById(R.id.webview)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                Log.d("MyApplication", "${message.message()} -- From line " +
                        "${message.lineNumber()} of ${message.sourceId()}")
                return true
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }

//            override fun onJsPrompt(
//                view: WebView?,
//                url: String?,
//                message: String?,
//                defaultValue: String?,
//                result: JsPromptResult?
//            ): Boolean {
//                // Check if the message is requesting a password
//                if (message == "PASSWORD_PROMPT") {
//                    val alertDialogBuilder = AlertDialog.Builder(this@YourActivity)
//                    alertDialogBuilder.setTitle("Enter Password")
//                    val input = EditText(this@YourActivity)
//                    alertDialogBuilder.setView(input)
//                    alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
//                        val password = input.text.toString()
//                        result?.confirm(password)
//                        dialog.dismiss()
//                    }
//                    alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
//                        result?.cancel()
//                        dialog.dismiss()
//                    }
//                    alertDialogBuilder.show()
//                    return true
//                }
//                return super.onJsPrompt(view, url, message, defaultValue, result)
//            }
        }


        // Enable JavaScript
        webView.settings.javaScriptEnabled = true

        // If JavaScript has access to user media
        webView.settings.mediaPlaybackRequiresUserGesture = false

        // Load your local HTML file from the assets directory
        webView.loadUrl("file:///android_asset/index.html")
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}
