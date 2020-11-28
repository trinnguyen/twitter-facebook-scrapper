# facebook-page-scrapper
Scrap public posts from Facebook Page using GeckoDriver


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
