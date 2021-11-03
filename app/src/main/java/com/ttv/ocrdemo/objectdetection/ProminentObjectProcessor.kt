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

package com.ttv.ocrdemo.objectdetection

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.os.Environment
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.graphics.toRect
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.ttv.ocrdemo.InputInfo
import com.ttv.ocrdemo.R
import com.ttv.ocrdemo.camera.CameraReticleAnimator
import com.ttv.ocrdemo.camera.FrameProcessorBase
import com.ttv.ocrdemo.camera.GraphicOverlay
import com.ttv.ocrdemo.camera.WorkflowModel
import com.ttv.ocrdemo.camera.WorkflowModel.WorkflowState
import com.ttv.ocrdemo.settings.PreferenceUtils
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.ttv.ocr.TTVOCRSdk
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.util.*
import java.util.concurrent.Callable
import android.graphics.Bitmap
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import java.lang.Byte.decode


/** A processor to run object detector in prominent object only mode.  */
class ProminentObjectProcessor(
    private val context: Context,
    graphicOverlay: GraphicOverlay,
    private val workflowModel: WorkflowModel,
    private val customModelPath: String) :
    FrameProcessorBase<List<DetectedObject>>() {

    private val confirmationController: ObjectConfirmationController = ObjectConfirmationController(graphicOverlay)
    private val cameraReticleAnimator: CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)
    private val reticleOuterRingRadius: Int = graphicOverlay
            .resources
            .getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_radius)

    init {
        val ocrSDK = TTVOCRSdk.CreateSDK(context)
        Log.e(TAG, "HWID: " + ocrSDK.GetCurrentHWID())
        ocrSDK.SetActivation("")
        ocrSDK.Create()
    }

    override fun stop() {
        super.stop()
        try {
        } catch (e: IOException) {
            Log.e(TAG, "Failed to close object detector!", e)
        }
    }

    internal class IDCard {
        var result:String? = null
        var rect:Rect? = null
        var portrait:String? = ""
        var title:String?= null
        var subTitle:String?= null
    }

    private fun extractCards(result: String): List<IDCard> {
        val cards: MutableList<IDCard> =
            LinkedList<IDCard>()

        if(result == null)
            return cards;

        try {
            var jsonResult = JSONObject(result)
            var posObj = jsonResult.get("position") as JSONObject
            var left = posObj.get("left") as Int
            var top = posObj.get("top") as Int
            var right = posObj.get("right") as Int
            var bottom = posObj.get("bottom") as Int

            var idCard = IDCard()
            idCard.rect = Rect(left, top, right, bottom)

            var label: String = ""
            var added: Int = 0
            if(jsonResult.has("name")) {
                label += jsonResult.getString("name")
            } else if(jsonResult.has("givenNames")) {
                label += jsonResult.getString("givenNames")
            } else if(jsonResult.has("surname")) {
                label += jsonResult.getString("surname")
            }

            val subTitleObj = JSONObject()

            for(i in 0..jsonResult.names().length() - 1) {
                val keyName:String = jsonResult.names().getString(i)
                if(keyName == "portrait" || keyName == "signature" || keyName == "ghostPortrait" || keyName == "name" || keyName == "position") {
                    continue
                }

                subTitleObj.put(keyName, jsonResult.getString(keyName))
            }

            if(jsonResult.has("portrait")) {
                var portrait = jsonResult.getString("portrait")
                val imageBytes = Base64.getDecoder()!!.decode(portrait)

                try {
                    var file = File(context.filesDir.absolutePath + "/tmp.png")
                    var os = FileOutputStream(file);
                    os.write(imageBytes);
                    os.close();

                    idCard.portrait = context.filesDir.absolutePath + "/tmp.png"
                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }

            idCard.title = label
            idCard.subTitle = subTitleObj.toString()
            idCard.result = result;

            cards.add(idCard)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cards
    }

    override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
        return Tasks.call(
            THREAD_POOL_EXECUTOR,
            Callable<List<DetectedObject>> {
                var results: MutableList<DetectedObject> = ArrayList()

                try {
                    var frameBmp = TTVOCRSdk.yuvToBitmap(image.byteBuffer.array(), image.width, image.height, image.width, image.height, image.rotationDegrees, false)
                    var result = TTVOCRSdk.GetIDSdk().Recognition(frameBmp)

                    if(result != null) {
                        val cardList = extractCards(result)

                        var i: Int = 0
                        for(card in cardList) {
                            val borderWidth: Float = 0.0f
                            // Transform corners
                            val cornerA = PointF(
                                card.rect!!.left - borderWidth,
                                card.rect!!.top - borderWidth
                            )
                            val cornerB = PointF(
                                card.rect!!.right + borderWidth,
                                card.rect!!.top - borderWidth
                            )
                            val cornerC = PointF(
                                card.rect!!.right + borderWidth,
                                card.rect!!.bottom + borderWidth
                            )
                            val cornerD = PointF(
                                card.rect!!.left - borderWidth,
                                card.rect!!.bottom + borderWidth
                            )

                            // Draw border
                            val pathBorder = Path()
                            pathBorder.moveTo(cornerA.x, cornerA.y)
                            pathBorder.lineTo(cornerB.x, cornerB.y)
                            pathBorder.lineTo(cornerC.x, cornerC.y)
                            pathBorder.lineTo(cornerD.x, cornerD.y)
                            pathBorder.lineTo(cornerA.x, cornerA.y)
                            pathBorder.close()

                            val rectF = RectF()
                            pathBorder.computeBounds(rectF, true)
                            if(rectF.left < 0)
                                rectF.left = 0.0f

                            if(rectF.top < 0)
                                rectF.top = 0.0f

                            if(rectF.right >= frameBmp.width)
                                rectF.right = (frameBmp.width - 1).toFloat()

                            if(rectF.bottom >= frameBmp.height)
                                rectF.bottom = (frameBmp.height - 1).toFloat()

                            val labels : MutableList<DetectedObject.Label> = ArrayList()
                            labels.add(DetectedObject.Label(card.title + "#" + card.subTitle + "#" + card.portrait, 0.toFloat(), 0))

                            val detectObj = DetectedObject(rectF.toRect(), i, labels)
                            results.add(detectObj)
                            i ++
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }


                results
            })
    }

    @MainThread
    override fun onSuccess(
        inputInfo: InputInfo,
        results: List<DetectedObject>,
        graphicOverlay: GraphicOverlay
    ) {
        var objects = results
        if (!workflowModel.isCameraLive) {
            return
        }

        if (PreferenceUtils.isClassificationEnabled(graphicOverlay.context)) {
            val qualifiedObjects = ArrayList<DetectedObject>()
            qualifiedObjects.addAll(objects)
            objects = qualifiedObjects
        }

        val objectIndex = 0
        val hasValidObjects = objects.isNotEmpty() &&
            (customModelPath == null || DetectedObjectInfo.hasValidLabels(objects[objectIndex]))
        if (!hasValidObjects) {
            confirmationController.reset()
            workflowModel.setWorkflowState(WorkflowState.DETECTING)
        } else {
            val visionObject = objects[objectIndex]
            if (objectBoxOverlapsConfirmationReticle(graphicOverlay, visionObject)) {
                // User is confirming the object selection.
                confirmationController.confirming(visionObject.trackingId)
                workflowModel.confirmingObject(
                        DetectedObjectInfo(visionObject, objectIndex, inputInfo), confirmationController.progress
                )
            } else {
                // Object detected but user doesn't want to pick this one.
                confirmationController.reset()
                workflowModel.setWorkflowState(WorkflowState.DETECTED)
            }
        }

        graphicOverlay.clear()
        if (!hasValidObjects) {
            graphicOverlay.add(ObjectReticleGraphic(graphicOverlay, cameraReticleAnimator))
            cameraReticleAnimator.start()
        } else {
            if (objectBoxOverlapsConfirmationReticle(graphicOverlay, objects[0])) {
                // User is confirming the object selection.
                cameraReticleAnimator.cancel()
                graphicOverlay.add(
                        ObjectGraphicInProminentMode(
                                graphicOverlay, objects[0], confirmationController
                        )
                )
                if (!confirmationController.isConfirmed &&
                    PreferenceUtils.isAutoSearchEnabled(graphicOverlay.context)) {
                    // Shows a loading indicator to visualize the confirming progress if in auto search mode.
                    graphicOverlay.add(ObjectConfirmationGraphic(graphicOverlay, confirmationController))
                }
            } else {
                // Object is detected but the confirmation reticle is moved off the object box, which
                // indicates user is not trying to pick this object.
                graphicOverlay.add(
                        ObjectGraphicInProminentMode(
                                graphicOverlay, objects[0], confirmationController
                        )
                )
                graphicOverlay.add(ObjectReticleGraphic(graphicOverlay, cameraReticleAnimator))
                cameraReticleAnimator.start()
            }
        }
        graphicOverlay.invalidate()
    }

    private fun objectBoxOverlapsConfirmationReticle(
        graphicOverlay: GraphicOverlay,
        visionObject: DetectedObject
    ): Boolean {
        val boxRect = graphicOverlay.translateRect(visionObject.boundingBox)
        val reticleCenterX = graphicOverlay.width / 2f
        val reticleCenterY = graphicOverlay.height / 2f
        val reticleRect = RectF(
                reticleCenterX - reticleOuterRingRadius,
                reticleCenterY - reticleOuterRingRadius,
                reticleCenterX + reticleOuterRingRadius,
                reticleCenterY + reticleOuterRingRadius
        )
        return reticleRect.intersect(boxRect)
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Object detection failed!", e)
    }

    companion object {
        private const val TAG = "ProminentObjProcessor"
    }
}
