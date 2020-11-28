import models.FbPost
import org.jsoup.Jsoup
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths

class FbPageParser: PageParser {

    override fun parseToFile(path: String, csvPath: String) {
        println("parsing $path")

        // get
        val doc = Jsoup.parse(Files.readString(Paths.get(path)))
        val elements = doc.select(".userContentWrapper")

        val list = mutableListOf<FbPost>()
        for (elem in elements) {
            // title
            val title = elem.select(".userContent p").firstOrNull()?.text() ?: continue

            // timestame
            val time = try {
                elem.select("abbr")?.firstOrNull()?.attr("data-utime")?.toLong() ?: 0
            } catch (ex: Exception) { 0 }

//            elem.select("abbr")?.firstOrNull()?.let {
//                val utime = it.attr("data-utime")
//                val time = it.attr("title")
//                val timetext = it.text()
//                println("\tshort: ${timetext}, long: $time, technical: $utime")
//            }

            // bottom
            val commentableItem = elem.select(".commentable_item")

            // reactions
            val reacts = try {
                commentableItem.select("a[data-testid=UFI2ReactionsCount/root] span")?.firstOrNull()?.text()?.toInt() ?: 0
            } catch (ex: Exception) { 0 }

            // comments
            val comments = try {
                commentableItem.select("a:contains(Comments)")?.firstOrNull()?.text()?.replace("Comments", "")?.trim()?.toInt() ?: 0
            } catch (ex: Exception) { 0 }

            // share
            val shares = try {
                commentableItem.select("a:contains(Shares)")?.firstOrNull()?.text()?.replace("Shares", "")?.trim()?.toInt() ?: 0
            } catch (ex: Exception) { 0 }

            // item
            val post = FbPost(title, time, reacts, comments, shares)
            list.add(post)
            println("\t: $post")
        }

        Util.writeFbPostToCsv(list, csvPath)
    }
}