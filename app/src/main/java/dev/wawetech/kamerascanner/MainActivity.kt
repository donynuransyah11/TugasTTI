package dev.wawetech.kamerascanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.widget.TextView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.Line
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import java.io.IOException

/**
 * Created by donynuransyah on 2/8/18.
 */
public class MainActivity : AppCompatActivity() {

    private lateinit var camerasource : CameraSource
    private val RequestCameraPermissionID = 1001
    private val TAG = javaClass.name
    private val log = AnkoLogger<MainActivity>()
//    private var mNumber: Int by Delegates.notNull<Int>()
//    var mNumber : StringBuilder? = StringBuilder("Yeay")
    private lateinit var TxtResult : TextView

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {


    when (requestCode){
        RequestCameraPermissionID -> if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            }
            try {
                camerasource.start(cameraView!!.holder)
            }catch (e : IOException){
                e.printStackTrace()
            }
        }
    }




    }

    fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
        if (value1 != null && value2 != null) {
            bothNotNull(value1, value2)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TxtResult = textview
        val textRecognizer : TextRecognizer = TextRecognizer.Builder(applicationContext).build()
        if(!textRecognizer.isOperational){
            Log.w(TAG,"Detector dependencies are not yet available")
        }else{
            camerasource = CameraSource.Builder(applicationContext,textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280,1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build()

            cameraView.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(Manifest.permission.CAMERA),
                                    RequestCameraPermissionID)
                        }
                        camerasource.start(cameraView!!.holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {

                }

                override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                    camerasource.stop()
                }
            })



            textRecognizer.setProcessor(object  : Detector.Processor<TextBlock>{

                override fun release() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }



                override fun receiveDetections(result : Detector.Detections<TextBlock>?) {
                    val items = result!!.detectedItems
                    if(items.size() != 0)
                    {
                        TxtResult.post {
                            val stringbuilder = StringBuilder()
                            for (i in 0 until items.size()){
                                val item = items.valueAt(i)
                                var lines : List<Line> = item.components as List<Line>

                                // /baris
                                for (elements:Line in lines){
                                    log.warn("baris : "+elements.value)
                                    log.warn("bahasa : "+elements.language)
                                }

                                //paragraf
                                log.info("bahasa : "+item.language)
                                log.info("paragraf : "+item.value)

                                stringbuilder.append(item.value)
                                stringbuilder.append("\n")

                            }
                            TxtResult.text = stringbuilder.toString()
                        }
                    }

                }

            })
        }
    }
}