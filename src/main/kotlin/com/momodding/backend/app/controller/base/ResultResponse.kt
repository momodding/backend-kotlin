package id.investree.app.config.base

data class ResultResponse<T>(
		var status: String,
		var data: T? = null,
		var meta: MetaResponse
)