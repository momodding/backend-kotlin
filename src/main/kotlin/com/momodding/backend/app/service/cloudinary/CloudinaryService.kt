package com.momodding.backend.app.service.cloudinary

import org.springframework.web.multipart.MultipartFile

interface CloudinaryService {

    fun uploadFile(file: MultipartFile): String
}