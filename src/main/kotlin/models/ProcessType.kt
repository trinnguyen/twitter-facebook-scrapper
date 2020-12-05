package models

enum class ProcessType {
    Facebook,
    Twitter,
    TwitterApi,
    TwitterSearch,
    None
}

fun String.toProcessType(): ProcessType {
    return when (this.toLowerCase()) {
        "facebook" -> ProcessType.Facebook
        "twitter" -> ProcessType.Twitter
        "twitter-api" -> ProcessType.TwitterApi
        "twitter-search" -> ProcessType.TwitterSearch
        else -> ProcessType.None
    }
}