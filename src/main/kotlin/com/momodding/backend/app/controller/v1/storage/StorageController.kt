package com.momodding.backend.app.controller.v1.storage

import com.momodding.backend.app.service.cloudinary.CloudinaryService
import id.investree.app.config.base.BaseController
import id.investree.app.config.base.ResultResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(value = ["v1/storage"], produces = [MediaType.APPLICATION_JSON_VALUE])
class StorageController @Autowired constructor(
        val cloudinaryService: CloudinaryService
) : BaseController() {

    @PostMapping("upload")
    fun upload(@RequestParam("file") file: MultipartFile): ResponseEntity<ResultResponse<Any>> {
        val result = HashMap<String, String>()
        result["urlFile"] = cloudinaryService.uploadFile(file)
        return generateResponse(result).done("upload sukses")
    }
}