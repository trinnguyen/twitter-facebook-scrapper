package scrapper

import parser.MobileFacebookParser

class MobileFacebookScrapper: PageScrapper(MobileFacebookParser()) {

    override fun getSource(): String {
        return ""
    }

}