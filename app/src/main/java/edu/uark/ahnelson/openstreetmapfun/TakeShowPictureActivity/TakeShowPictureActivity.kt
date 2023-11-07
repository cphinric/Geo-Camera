package edu.uark.ahnelson.openstreetmapfun.TakeShowPictureActivity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.uark.ahnelson.openstreetmapfun.R
import edu.uark.ahnelson.openstreetmapfun.Util.LocationUtilCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import android.content.ContentValues
import android.content.Context
import android.widget.EditText
import android.widget.Toast
import edu.uark.ahnelson.openstreetmapfun.Model.Marker
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

class TakeShowPictureActivity : AppCompatActivity() {

    var currentPhotoPath: String = ""
    lateinit var imageView: ImageView
    lateinit var editTextDescription: EditText
    private var geoPhotoId:Int = -1
    //private var getMarkerId:Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_show_picture)
        imageView = findViewById(R.id.ivPictureFrame)
        editTextDescription = findViewById(R.id.textInputEditText)


        val intent = getIntent()
        geoPhotoId = intent.getIntExtra("GEOPHOTO_ID", -1)
        //getMarkerId = intent.getIntExtra("MARKER_ID", -1)

        if (geoPhotoId == -1) {
            takeAPicture()
        } else {
            currentPhotoPath = intent.getStringExtra("GEOPHOTO_LOC").toString()
        }

        findViewById<FloatingActionButton>(R.id.fabDelete).setOnClickListener {
            // Only proceed if getMarkerId is not -1
//            if (getMarkerId != -1) {
//                val deleteIntent = Intent()
//                deleteIntent.putExtra("DELETE_MARKER_ID", getMarkerId)
//                setResult(Activity.RESULT_OK, deleteIntent)
//                finish()
//            } else {
//                Toast.makeText(this, "No valid marker ID provided", Toast.LENGTH_SHORT).show()
//                finish()
//            }

            finish()
        }

        findViewById<FloatingActionButton>(R.id.fabSave).setOnClickListener {
            // Save the image to the gallery
            val photoFile = File(currentPhotoPath)
            saveImageToGallery(this, photoFile)

            // Prepare the intent to return the photo path
            val retIntent: Intent = Intent()
            retIntent.putExtra("GEOPHOTO_LOC", currentPhotoPath)
            setResult(RESULT_OK, retIntent)

            // Optional: Notify the user that the image has been saved
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()

            val returnIntent = Intent()
            val description = editTextDescription.text.toString()
            returnIntent.putExtra("IMAGE_PATH", currentPhotoPath)
            returnIntent.putExtra("DESCRIPTION", description) // Get description from user input
            setResult(Activity.RESULT_OK, returnIntent)

            // Finish the activity
            finish()
        }
    }


    override fun onResume() {
        super.onResume()
        if(geoPhotoId != -1){
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    Thread.sleep(200)
                    withContext(Dispatchers.Main){
                        setPic()
                    }
                }
            }
        }
    }
    private val takePictureResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_CANCELED){
            Log.d("MainActivity","Take Picture Activity Cancelled")
        }else{
            Log.d("MainActivity", "Picture Taken")
            setPic()
        }
    }

    private fun setPic() {
        // Get the width of the ImageView
        val targetW: Int = imageView.width

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Check if the photo dimensions are valid
        if (photoW <= 0 || photoH <= 0) {
            Log.e("TakeShowPictureActivity", "Invalid image dimensions")
            return
        }

        val photoRatio: Double = photoH.toDouble() / photoW.toDouble()
        val targetH: Int = (targetW * photoRatio).roundToInt()

        // Determine how much to scale down the image
        val scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

        // Decode the image file into a Bitmap sized to fill the ImageView
        bmOptions.apply {
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        imageView.setImageBitmap(bitmap)
    }


    private fun takeAPicture() {
        val picIntent: Intent=Intent().setAction(MediaStore.ACTION_IMAGE_CAPTURE)
        if(picIntent.resolveActivity(packageManager) != null){
            val filepath: String = createFilePath()
            val myFile: File = File(filepath)
            currentPhotoPath = filepath
            val photoUri = FileProvider.getUriForFile(this,"edu.ark.nelson.OpenStreetMap.file-provider",myFile)
            picIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
            takePictureResultLauncher.launch(picIntent)
        }
    }

    private fun createFilePath(): String {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intent
        currentPhotoPath = image.absolutePath
        return image.absolutePath
    }

    private fun saveImageToGallery(context: Context, file: File) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let { contentUri ->
            context.contentResolver.openOutputStream(contentUri)?.use { outputStream ->
                FileInputStream(file).use { fileInputStream ->
                    fileInputStream.copyTo(outputStream)
                }
            } ?: Log.e("SaveImage", "Failed to get OutputStream")
        }

        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }

}