import org.apache.commons.cli.*
import java.io.File
import java.net.URISyntaxException
import java.net.URL
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

    // scrap page
    if (!process(cli, path)) {
        exitProcess(1)
    }
}

fun process(cli: CommandLine, path: String): Boolean {
    val isTwitter = cli.hasOption('t') && cli.getOptionValue('t') == "twitter"

    if (isValidUrl(path)) {
        return scrapPage(isTwitter, cli, path)
    }

    if (isHtmlFile(path)) {
        return parsePage(isTwitter, path)
    }

    return false
}

fun scrapPage(isTwitter: Boolean, cli: CommandLine, path: String): Boolean {
    val folder = File(File("").absolutePath)
    val child = Paths.get(folder.absolutePath, "geckodriver")
    if (!Files.exists(child)) {
        println("geckodriver is not exist, auto download to ${child}")
        if (!Util.downloadGeckoDriver(folder))
            return false
    }

    val profileName = if (cli.hasOption('p')) cli.getOptionValue('p') else "FbScrapper"
    System.setProperty("webdriver.firefox.driver", child.toAbsolutePath().toString());
    System.setProperty("webdriver.firefox.profile", profileName);

    val number = if (cli.hasOption('n')) {
        cli.getOptionValue('n').toIntOrNull() ?: Int.MAX_VALUE
    } else {
        Int.MAX_VALUE
    }
    val scrapper: PageScrapper = if (isTwitter) TwitterScrapper() else FbPageScrapper()
    val files = scrapper.exec(path, number)
    if (files.isNullOrEmpty()) return false

    // parse page
    return parsePage(isTwitter, files.last())
}

fun parsePage(isTwitter: Boolean, path: String): Boolean {
    val parser: PageParser = if (isTwitter) TwitterParser() else FbPageParser()
    val csvPath = Util.getCsvFile(path) ?: return false
    parser.parseToFile(path, csvPath)
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
    if (!isValidUrl(path) && !isHtmlFile(path))
        return "a valid URL or HTML file path must be specified, example: https://twitter.com/samsung"

    return null
}

fun isValidUrl(path: String): Boolean {
    if (path.startsWith("http://") || path.startsWith("https://")) {
        return try {
            val url = URL(path);
            url.toURI()
            true
        } catch (ex: URISyntaxException) {
            false
        }
    }

    return false
}

fun isHtmlFile(path: String): Boolean {
    return Files.exists(Paths.get(path)) && (path.endsWith(".html") || path.endsWith(".htm"))
}

fun printError(s: String) {
    System.err.println("error: $s")
}

fun createOptions(): Options {
    val options = Options()
    options.addOption("n", true, "Number of scrolls. Default is infinitely")
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
}
