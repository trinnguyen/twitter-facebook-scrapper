package parser

import Util
import models.FbPost
import org.jsoup.Jsoup

class FbPageParser: PageParser {

    override fun parseToCsv(source: String, csvPath: String): Boolean {
        try {
            // get
            val doc = Jsoup.parse(source)
            val elements = doc.select(".userContentWrapper")

            val list = mutableListOf<FbPost>()
            for (elem in elements) {
                // title
                val title = elem.select(".userContent p").firstOrNull()?.text() ?: continue

                // timestame
                val time = try {
                    elem.select("abbr")?.firstOrNull()?.attr("data-utime")?.toLong() ?: 0
                } catch (ex: Exception) {
                    0
                }

                // bottom
                val commentableItem = elem.select(".commentable_item")

                // reactions
                val reacts = try {
                    commentableItem.select("a[data-testid=UFI2ReactionsCount/root] span")?.firstOrNull()?.text()
                        ?.toInt() ?: 0
                } catch (ex: Exception) {
                    0
                }

                // comments
                val comments = try {
                    commentableItem.select("a:contains(Comments)")?.firstOrNull()?.text()?.replace("Comments", "")
                        ?.trim()?.toInt() ?: 0
                } catch (ex: Exception) {
                    0
                }

                // share
                val shares = try {
                    commentableItem.select("a:contains(Shares)")?.firstOrNull()?.text()?.replace("Shares", "")?.trim()
                        ?.toInt() ?: 0
                } catch (ex: Exception) {
                    0
                }

                // item
                val post = FbPost(title, time, reacts, comments, shares)
                list.add(post)
            }

            Util.writeFbPostToCsv(list, csvPath)
            println("\t found ${list.size} Facebook posts")
            if (list.isNotEmpty()) {
                println("\t last post: ${list.last().formatCsv()}")
            }
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return false
    }
}