import org.apache.commons.cli.*
import parser.FbPageParser
import scrapper.FbPageScrapper
import parser.PageParser
import parser.TwitterParser
import scrapper.PageScrapper
import scrapper.TwitterScrapper
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    // parse and validate
    val opts = createOptions()
    val cli = parseCli(opts, args) ?: exitProcess(1)
    ensureValidArgs(cli)?.let {
        printError(it)
        printHelp(opts)
        exitProcess(1)
    }

    val path = cli.argList.first()

    ULogger.setup(path)

    // scrap page
    if (!process(cli, path)) {
        exitProcess(1)
    }
}

fun process(cli: CommandLine, path: String): Boolean {
    // detect twitter or facebook
    val isTwitter = if (cli.hasOption('t')) {
        cli.getOptionValue('t') == "twitter"
    } else {
        Util.isTwitterPath(path)
    }

    // check if scrapping or parsing
    if (Util.isValidUrl(path)) {
        return scrapPage(isTwitter, cli, path)
    }

    if (Util.isHtmlFile(path)) {
        return parsePage(isTwitter, path)
    }

    return false
}

fun scrapPage(isTwitter: Boolean, cli: CommandLine, path: String): Boolean {
    val folder = File(File("").absolutePath)
    val child = Paths.get(folder.absolutePath, "geckodriver")
    if (!Files.exists(child)) {
        ULogger.logInfo("geckodriver is not exist, auto download to ${child}")
        if (!Util.downloadGeckoDriver(folder))
            return false
    }

    val profileName = if (cli.hasOption('p')) cli.getOptionValue('p') else "FbScrapper"
    System.setProperty("webdriver.firefox.driver", child.toAbsolutePath().toString());
    System.setProperty("webdriver.firefox.profile", profileName);

    val countCsv = getCountCsv(cli, 10)

    val scrapper: PageScrapper = if (isTwitter) TwitterScrapper() else FbPageScrapper()
    val file: String? = scrapper.exec(path, countCsv)
    return !file.isNullOrEmpty()
}

fun getCountCsv(cli: CommandLine, def: Int): Int {
    if (cli.hasOption('n')) {
        return try {
            val value = cli.getOptionValue('n')
            value.toInt()
        } catch (ex: Exception) {
            def
        }
    }

    return def
}

fun parsePage(isTwitter: Boolean, path: String): Boolean {
    val parser: PageParser = if (isTwitter) TwitterParser() else FbPageParser()
    parser.parseHtmlFileToCsv(path)
    return true
}

fun ensureValidArgs(cli: CommandLine): String? {
    if (cli.hasOption('t')) {
        val value = cli.getOptionValue('t')
        if (value != "twitter" && value != "facebook") {
            return "type must be twitter or facebook, example '-t twitter'"
        }
    }

    if (cli.argList.isNullOrEmpty())
        return "missing URL or HTML file path"

    val path = cli.argList.first()
    if (!Util.isValidUrl(path) && !Util.isHtmlFile(path))
        return "a valid URL or HTML file path must be specified, example: https://twitter.com/samsung"

    return null
}



fun printError(s: String) {
    System.err.println("error: $s")
}

fun createOptions(): Options {
    val options = Options()
    options.addOption("n",
        "type",
        true,
        "number of recent csv files to keep, default is 10")
    options.addOption("t",
        "type",
        true,
        "'facebook' or 'twitter'. Default is 'facebook'")
    options.addOption(
        "p",
        "profile",
        true,
        "Firefox profile name, use about:profiles on Firefox and create new profile. Default is 'FbScrapper'"
    )
    return options
}

fun parseCli(opts: Options, args: Array<String>): CommandLine? {

    val parser: CommandLineParser = DefaultParser()
    try {
        return parser.parse(opts, args);
    } catch (ex: ParseException) {
        printHelp(opts)
    }

    return null
}

fun printHelp(options: Options) {
    HelpFormatter().printHelp("fb-scrapper", options)

    println("")
    println("examples:")
    println("  fb-scrapper https://twitter/samsung")
    println("  fb-scrapper -n 50 https://www.facebook.com/samsungelectronics")
    println("  fb-scrapper src-gen/facebook_com/mashable/200.html")
}
