package com.momodding.backend.app.service.cloudinary

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream

@Service
class CloudinaryServiceImpl @Autowired constructor(
        val cloudinaryConfig: Cloudinary
) : CloudinaryService {
    override fun uploadFile(file: MultipartFile): String {
        return try {
            val uploadedFile: File = convertMultipartToFile(file)
            val uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap())
            uploadResult["url"].toString()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun convertMultipartToFile(file: MultipartFile): File {
        val convFile = File(file.originalFilename)
        val fos = FileOutputStream(convFile)
        fos.write(file.bytes)
        fos.close()
        return convFile
    }
}