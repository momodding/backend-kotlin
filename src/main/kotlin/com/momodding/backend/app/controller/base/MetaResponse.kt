package id.investree.app.config.base

data class MetaResponse(
        var code: Int,
        var message: Any?,
        var debugInfo: String? = ""
)