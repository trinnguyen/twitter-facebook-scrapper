package scrapper

import parser.TwitterParser

class TwitterScrapper : PageScrapper(TwitterParser()) {

    override fun onScrolling() {
    }
}