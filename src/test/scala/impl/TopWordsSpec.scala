package edu.luc.cs.cs371.topwords
package impl

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ArrayBuffer

final class CollectingObserver extends Observer:
  val events: ArrayBuffer[Stats] = ArrayBuffer.empty
  override def onStats(stats: Stats): Unit = events.append(stats)

class TopWordsSpec extends AnyFunSuite:

  test("no stats before window is full") {
    val obs = CollectingObserver()
    val eng = TopWordsManager(howMany = 3, minLength = 2, windowSize = 5, observer = obs)

    List("aa", "bb", "cc", "aa").foreach(eng.accept)
    assert(obs.events.isEmpty)
  }

  test("counts only within moving window") {
    val obs = CollectingObserver()
    val eng = TopWordsManager(howMany = 3, minLength = 2, windowSize = 5, observer = obs)

    // "a" ignored due to minLength=2
    List("a", "aa", "bb", "cc", "aa", "bb").foreach(eng.accept)

    val last = obs.events.last.top.toMap
    assert(last("aa") == 2)
    assert(last("bb") == 2)
    assert(last("cc") == 1)
  }

  test("tie-breaker is deterministic") {
    val obs = CollectingObserver()
    val eng = TopWordsManager(howMany = 2, minLength = 2, windowSize = 4, observer = obs)

    List("bb", "aa", "bb", "aa").foreach(eng.accept)
    assert(obs.events.last.top == Vector(("aa", 2), ("bb", 2)))
  }
