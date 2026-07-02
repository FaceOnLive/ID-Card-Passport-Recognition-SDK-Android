package com.bio.idcardrecognition

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*


class ResultActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 1

    private lateinit var imagePager: ViewPager // creating object of ViewPager
    private lateinit var imageTab: TabLayout  // creating object of TabLayout
    private lateinit var listView: ListView

    companion object {
        lateinit var overviewBmp : Bitmap
        lateinit var resultString: String
        lateinit var profileBmp : Bitmap
        lateinit var nameTxt: String
        lateinit var typeTxt: String
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        profileBmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Reset Bitmap
        nameTxt = "" // Reset to empty string
        typeTxt = "" // Reset to empty string

        imagePager = findViewById(R.id.image_pager)
        imageTab = findViewById(R.id.image_tabs)
        val result:String = resultString

        val sections = mutableListOf<Section>()
        val ocrSection = Section("OCR Result")
        sections.add(ocrSection)
        try {
            var jsonResult = JSONObject(result)
            val keys: MutableIterator<String> = jsonResult.keys()

            while(keys.hasNext()) {
                var key = keys.next() as String
                if(key == getString(R.string.full_name)) {
                    val value = jsonResult.get(key).toString()
                    nameTxt = value
                }

                if(key == getString(R.string.images)) {
                    val imagesObj = jsonResult.get(key) as JSONObject
                    val imagesKeys: MutableIterator<String> = imagesObj.keys()

                    while (imagesKeys.hasNext()) {
                        val imageKey = imagesKeys.next() as String
                        val imageValue = imagesObj.get(imageKey).toString()
                        val imageBytes = Base64.getDecoder()!!.decode(imageValue)

                        try {
                            val bitmap =
                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            if (imageKey == getString(R.string.portrait)) {
                                profileBmp = bitmap
                            } else if (imageKey == getString(R.string.document)) {
                                overviewBmp = bitmap
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else if(key == getString(R.string.position)) {

                } else if(key == getString(R.string.quality)) {

                } else if(key == getString(R.string.mrz)) {
                    val mrzSection = Section("MRZ Result")
                    val mrzObj = jsonResult.get(key) as JSONObject
                    val mrzKeys: MutableIterator<String> = mrzObj.keys()

                    while (mrzKeys.hasNext()) {
                        val mrzKey = mrzKeys.next() as String
                        val mrzValue = mrzObj.get(mrzKey).toString()
                        mrzSection.addItem(Item(mrzKey, mrzValue))
                    }
                    sections.add(mrzSection)
                } else if(key == getString(R.string.documentName)) {
                    var value = jsonResult.get(key).toString()

                    if(jsonResult.has(getString(R.string.issuingStateCode))) {
                        val stateCode = jsonResult.get(getString(R.string.issuingStateCode)).toString()
                        value = stateCode + " - " + value
                    }
                    typeTxt = value
                    ocrSection.addItem(Item(key, value))
                } else {
                    var value = jsonResult.get(key).toString()
                    ocrSection.addItem(Item(key, value))
                }
            }

            val imagePagerAdapter = ImageViewPagerAdapter(supportFragmentManager)
            imagePagerAdapter.addFragment(ResultOverviewFragment(), "Document")
            imagePagerAdapter.addFragment(ResultPortraitFragment(), "Portrait")
            imagePager.adapter = imagePagerAdapter
            imageTab.setupWithViewPager(imagePager)

            val recyclerView = findViewById<RecyclerView>(R.id.result_view)
            recyclerView.layoutManager = LinearLayoutManager(this)
            val sectioned_adapter = SectionedAdapter(sections)
            recyclerView.adapter = sectioned_adapter
            recyclerView.addItemDecoration(StickyHeaderDecoration(sectioned_adapter))

        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R ) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun Bitmap.toBytes(): ByteArray {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}