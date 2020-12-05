import ULogger.logInfo
import org.apache.commons.cli.*
import twitter4j.Paging
import twitter4j.Status
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
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

    Util.generateLogPath(path)?.let { ULogger.setup(it) }

    // scrap page
    if (!process(cli, path)) {
        exitProcess(1)
    }
}

fun process(cli: CommandLine, path: String): Boolean {

    // check if scrapping or parsing
    if (Util.isValidUrl(path)) {
        return scrapPage(cli, path)
    }

    if (Util.isHtmlFile(path)) {
        // detect twitter or facebook
        val provider = if (cli.hasOption('t')) cli.getOptionValue('t') else ""
        val parser = Factory.createParser(provider)
        parser.parseHtmlFileToCsv(path)
        return true
    }

    return false
}

fun scrapPage(cli: CommandLine, path: String): Boolean {
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

    val scrapper = Factory.createScrapper(path)
    val file: String? = scrapper.exec(path)
    return !file.isNullOrEmpty()
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
    options.addOption(
        "t",
        "type",
        true,
        "'facebook' or 'twitter'. Default is 'facebook'"
    )
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
    println("  fb-scrapper https://twitter.com/samsung")
    println("  fb-scrapper https://facebook.com/mashable")
    println("  fb-scrapper -p FbScrapper https://facebook.com/mashable")
    println("  fb-scrapper src-gen/facebook_com/mashable/200.html")
}
