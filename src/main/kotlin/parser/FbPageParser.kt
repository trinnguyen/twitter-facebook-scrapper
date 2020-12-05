package parser

import Util.normalizeNumber
import models.CsvRow
import models.FbPost
import org.jsoup.Jsoup

class FbPageParser: PageParser() {

    override fun parseToRows(source: String): List<CsvRow> {
        try {
            // get
            val doc = Jsoup.parse(source)
            val elements = doc.select("div[role='article']")

            val list = mutableListOf<FbPost>()
            for (elem in elements) {
                val id: String = elem.attr("id")

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
                val reacts = commentableItem.select("a[data-testid=UFI2ReactionsCount/root] span")?.firstOrNull()?.text()

                // comments
                val comments = commentableItem.select("a:contains(Comments)")?.firstOrNull()?.text()?.replace("Comments", "")

                // share
                val shares = commentableItem.select("a:contains(Shares)")?.firstOrNull()?.text()?.replace("Shares", "")?.trim()

                // item
                val post = FbPost(id, title, time, reacts.normalizeNumber(), comments.normalizeNumber(), shares.normalizeNumber())
                list.add(post)
            }

            return list
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return emptyList()
    }
}