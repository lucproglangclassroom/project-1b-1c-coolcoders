package edu.luc.cs.cs371.topwords
package main

import edu.luc.cs.cs371.topwords.impl.*
import mainargs.{Flag, ParserForMethods, arg, main}
import org.slf4j.LoggerFactory

object TopWords:

  private val log = LoggerFactory.getLogger(getClass)

  @main
  def run(
      @arg(name = "cloud-size", short = 'c', doc = "size of word cloud")
      cloudSize: Int = 10,

      @arg(name = "length-at-least", short = 'l', doc = "minimum word length")
      lengthAtLeast: Int = 6,

      @arg(name = "window-size", short = 'w', doc = "size of moving window")
      windowSize: Int = 1000,

      @arg(name = "ignore-case", short = 'i', doc = "treat words case-insensitively")
      ignoreCase: Flag,

      @arg(name = "every-k-steps", short = 'k', doc = "update word cloud only every k accepted words")
      everyKSteps: Int = 1,

      @arg(name = "min-frequency", short = 'f', doc = "minimum frequency to include in the word cloud")
      minFrequency: Int = 1

  ): Unit =
    require(cloudSize > 0 && lengthAtLeast > 0 && windowSize > 0)

    log.debug(s"howMany=$cloudSize minLength=$lengthAtLeast lastNWords=$windowSize")

    val cfg = TopWordsFunctional.Config(
      howMany = cloudSize,
      minLength = lengthAtLeast,
      windowSize = windowSize,
      caseInsensitive = ignoreCase.value,
      everyKSteps = everyKSteps,
      minFrequency = minFrequency
    )

    val engine = TopWordsFunctional.engine(cfg)

    val lines = scala.io.Source.stdin.getLines
    val words =
      import scala.language.unsafeNulls
      lines
        .flatMap(_.split("(?U)[^\\p{Alpha}0-9']+"))
        .filter(_.nonEmpty)

    engine.process(words).foreach { stats =>
      val out =
        stats.top
          .map { case (word, freq) => s"$word: $freq" }
          .mkString(" ")

      println(out)

      if scala.sys.process.stdout.checkError() then
        sys.exit(0)
    }

  def main(args: Array[String]): Unit =
    ParserForMethods(this).runOrExit(args.toIndexedSeq)
    ()


  //NON FUNCTIONAL
  //   val observer = ConsoleObserver()
  //   val engine = TopWordsManager(
  //     howMany = cloudSize,
  //     minLength = lengthAtLeast,
  //     windowSize = windowSize,
  //     observer = observer,
  //     caseInsensitive = ignoreCase.value,
  //     everyKSteps = everyKSteps,
  //     minFrequency = minFrequency
  //   )

  //   val lines = scala.io.Source.stdin.getLines
  //   val words =
  //     import scala.language.unsafeNulls
  //     lines
  //       .flatMap(_.split("(?U)[^\\p{Alpha}0-9']+"))
  //       .filter(_.nonEmpty)

  //   words.foreach(engine.accept)

  // def main(args: Array[String]): Unit =
  //   ParserForMethods(this).runOrExit(args.toIndexedSeq)
  //   ()
