# twitter-facebook-scrapper
Scrapper tool to collect the public posts from Facebook and tweets from Twitter using Selenium Firebase WebDriver

## Features
- Collect unlimitted public Twitter tweets of a specific profile
- Collect unlimitted public Facebook posts of a page
- Output entries to CSV file in real-time
- Developed in Kotlin, targets JVM, use geckodriver (a WebDriver for Firefox)
- Cross-platform tool run on macOS, Windows, Linux

## usage
```shell
usage: fb-scrapper
 -n <arg>             Number of scrolls. Default is infinitely
 -p,--profile <arg>   Firefox profile name, use about:profiles on Firefox
                      and create new profile. Default is 'FbScrapper'
 -t,--type <arg>      'facebook' or 'twitter'. Default is 'facebook'

examples:
  fb-scrapper -n 10 https://www.facebook.com/samsungelectronics
  fb-scrapper src-gen/facebook_com/mashable/200.html
```
