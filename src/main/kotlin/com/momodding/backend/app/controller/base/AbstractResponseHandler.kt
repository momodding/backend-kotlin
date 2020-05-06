package id.investree.app.config.base

import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

abstract class AbstractResponseHandler {

    fun done(msg: String? = "", httpStatus: HttpStatus = HttpStatus.OK): ResponseEntity<ResultResponse<Any>> {

        return when (val processResponse = data()) {
            is Exception -> onError(msg, processResponse, httpStatus)
            else -> onSuccess(msg, processResponse, httpStatus)
        }
    }

    private fun onError(msg: String?, ex: Exception, httpStatus: HttpStatus): ResponseEntity<ResultResponse<Any>> {
        val metaResponse = MetaResponse(
            code = httpStatus.value(),
            message = msg,
            debugInfo = ExceptionUtils.getStackTrace(ex).split("\n")[0]
        )

        val result = ResultResponse<Any>(
            status = "ERROR",
            meta = metaResponse
        )
        return generateResponseEntity(result, httpStatus)
    }

    private fun onSuccess(msg: String?, any: Any, httpStatus: HttpStatus): ResponseEntity<ResultResponse<Any>> {
        val metaResponse = MetaResponse(
            code = httpStatus.value(),
            message = msg
        )

        val result = ResultResponse(
            status = "OK",
            data = any,
            meta = metaResponse
        )
        return generateResponseEntity(result, httpStatus)
    }

    private fun generateResponseEntity(result: ResultResponse<Any>, code: HttpStatus) =
        ResponseEntity(result, HttpHeaders().apply {
            set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }, code)

    abstract fun data(): Any
}