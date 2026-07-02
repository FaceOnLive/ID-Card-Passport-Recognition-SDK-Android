package com.bio.idcardrecognition

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bio.idsdk.IDSDK
import org.json.JSONObject
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    companion object {
        private val SELECT_PHOTO_REQUEST_CODE = 1
        private val IDCARD_RECOGNITION_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Activate the SDK with your license key (see Config.kt), then initialize it.
        var ret = IDSDK.setActivation(Config.LICENSE_KEY)
        if (ret == IDSDK.SDK_SUCCESS) {
            ret = IDSDK.init(this)
        }
        if (ret != IDSDK.SDK_SUCCESS) {
            Toast.makeText(this, "SDK activation/init failed (code $ret)", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnCamera).setOnClickListener {
            startActivityForResult(Intent(this, CameraActivityKt::class.java), IDCARD_RECOGNITION_REQUEST_CODE)
        }

        findViewById<Button>(R.id.btnGallery).setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == SELECT_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                var bitmap: Bitmap = Utils.getCorrectlyOrientedImage(this, data?.data!!)
                val result = IDSDK.idcardRecognition(bitmap)
                if(result != null) {
                    var jsonResult = JSONObject(result)
                    if(jsonResult.get(getString(R.string.documentName)) != "Unknown") {
                        ResultActivity.resultString = result

                        val intent = Intent(this, ResultActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Failed to recognition!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to recognition!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: java.lang.Exception) {
                //handle exception
                e.printStackTrace()
            }
        }
    }
}