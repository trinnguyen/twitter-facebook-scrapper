import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import org.slf4j.LoggerFactory


object ULogger {

    private val ctx = LoggerFactory.getILoggerFactory() as LoggerContext

    var logger: ch.qos.logback.classic.Logger = ctx.getLogger(ULogger.javaClass.name)

    fun setup(logPath: String) {
        println("logging to $logPath")

        val ple = PatternLayoutEncoder()
        ple.pattern = "%-12date{YYYY-MM-dd HH:mm:ss.SSS} %-5level - %msg%n";
        ple.context = ctx
        ple.start()

        val fileAppender = FileAppender<ILoggingEvent>()
        fileAppender.file = logPath;
        fileAppender.encoder = ple
        fileAppender.context = ctx
        fileAppender.start()

        val logConsoleAppender: ConsoleAppender<ILoggingEvent> = ConsoleAppender<ILoggingEvent>()
        logConsoleAppender.context = ctx
        logConsoleAppender.name = "console"
        logConsoleAppender.encoder = ple
        logConsoleAppender.start()


        logger.level = Level.ALL
        logger.addAppender(logConsoleAppender)
        logger.addAppender(fileAppender)
        logger.isAdditive = false
    }

    fun logInfo(msg: String) {
        logger.info(msg)
    }

    fun logError(msg: String) {
        logger.error(msg)
    }

    fun logException(ex: Exception) {
        logger.error(ex.message + "\n" + ex.stackTrace)
    }
}