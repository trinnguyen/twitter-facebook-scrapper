
interface PageScrapper {
    abstract fun exec(url: String, number: Int): List<String>
}