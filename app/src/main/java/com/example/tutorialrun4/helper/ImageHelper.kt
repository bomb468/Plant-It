package com.example.tutorialrun4.helper

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageHelper {
    fun saveImageToInternalStorage(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val folder = File(context.getExternalFilesDir(null), "plant_images")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, "plant_${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file
    }

    fun getTmpFileUri(context: Context): Uri {
        val folder = File(context.getExternalFilesDir(null), "tmp_images")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val tmpFile = File(folder, "tmp_image_${UUID.randomUUID()}.jpg")
        
        val authority = "${context.packageName}.fileprovider"
        return androidx.core.content.FileProvider.getUriForFile(context, authority, tmpFile)
    }
    
    fun deleteImage(path: String?) {
        if (path.isNullOrEmpty()) return
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }
}
