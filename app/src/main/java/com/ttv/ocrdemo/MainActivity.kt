/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ttv.ocrdemo

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView


/** Entry activity to select the detection mode.  */
class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback{

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_main)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun startMain() {
        val thread = Thread {
            try {
                var waited = 0
                // Splash screen pause time
                while (waited < 1500) {
                    Thread.sleep(100)
                    waited += 100
                }
                startActivity(Intent(applicationContext, com.ttv.ocrdemo.CustomModelObjectDetectionActivity::class.java))
                finish()
            } catch (e: InterruptedException) {
                // do nothing
            } finally {
                this@MainActivity.finish()
            }
        }
        thread.start()
    }

    override fun onResume() {
        super.onResume()
        if (!Utils.allPermissionsGranted(this)) {
            Utils.requestRuntimePermissions(this, 101)
        } else {
            startMain()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 101) {
            if(permissions.size > 1) {
                startMain()
            }
        }
    }
}
