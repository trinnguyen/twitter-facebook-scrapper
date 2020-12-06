import models.ProcessType
import models.toProcessType
import org.apache.commons.cli.*
import java.io.File
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
        val parser = Factory.createParser(cli.getProcessType())
        parser.parseHtmlFileToCsv(path)
        return true
    }

    return false
}

fun scrapPage(cli: CommandLine, path: String): Boolean {
    val folder = File(File("").absolutePath)
    val driver = Factory.createWebDriver(cli, folder)

    val scrapper = Factory.createScrapper(cli, path)
    val file: String? = scrapper.exec(path, driver)
    return !file.isNullOrEmpty()
}

fun ensureValidArgs(cli: CommandLine): String? {
    if (cli.hasOption('t')) {
        if (cli.getOptionValue('t', "").toProcessType() == ProcessType.None) {
            return "invalid -t value"
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
        true,
        "Value: facebook, twitter, twitter-api, twitter-search"
    )
    options.addOption(
        "p",
        true,
        "Firefox profile name, use about:profiles on Firefox and create new profile. Default is 'FbScrapper'"
    )
    options.addOption("d",
        true,
        "Use 'chrome' or 'firefox'. Default is 'firefox'")
    options.addOption("s",
        true,
        "Scrolling speed for Twitter only, default is 1. Example value: 1.5 or 2")
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
    println("  fb-scrapper -t twitter https://twitter.com/samsung")
    println("  fb-scrapper -t twitter-api https://twitter.com/samsung")
    println("  fb-scrapper https://facebook.com/mashable")
}

fun CommandLine.getProcessType(): ProcessType {
    return this.getOptionValue('t', "").toProcessType()
}

fun CommandLine.isChromeDriver(): Boolean {
    return this.getOptionValue('d', "firefox").equals("chrome", true)
}

fun CommandLine.getTwitterScrollingSpeed(): Long {
    val def: Long = 3600
    return this.getOptionValue('s', def.toString()).toLongOrNull() ?: def
}