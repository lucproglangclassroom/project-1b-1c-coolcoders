package edu.luc.cs.cs371.topwords
package impl

final class ConsoleObserver extends Observer:
  override def onStats(stats: Stats): Unit =
    val output = stats.top.map { case (word, frequency) => s"$word: $frequency" }.mkString(" ")
    println(output)
    if scala.sys.process.stdout.checkError() then
      sys.exit(1)
