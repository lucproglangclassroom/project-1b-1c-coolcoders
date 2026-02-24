package edu.luc.cs.cs371.topwords
package impl

import scala.collection.mutable

final class TopWordsManager(
    howMany: Int,
    minLength: Int,
    windowSize: Int,
    observer: Observer,
    caseInsensitive: Boolean = false,
    everyKSteps: Int = 1,
    minFrequency: Int = 1
):

  require(howMany > 0)
  require(minLength > 0)
  require(windowSize > 0)
  require(everyKSteps > 0)
  require(minFrequency > 0)

  private val window = mutable.ArrayDeque.empty[String]
  private val counts = mutable.HashMap.empty[String, Int].withDefaultValue(0)
  private var acceptedCount = 0

  def accept(raw: String): Unit =
    if raw == null then return

    val word = if caseInsensitive then raw.toLowerCase.nn else raw
    if word.length < minLength then return

    acceptedCount += 1

    window.append(word)
    counts.update(word, counts(word) + 1)

    if window.size > windowSize then
      val old = window.removeHead()
      val newCount = counts(old) - 1
      if newCount <= 0 then
        counts.remove(old): Unit
      else
        counts.update(old, newCount)

    if window.size == windowSize && acceptedCount % everyKSteps == 0 then
      val topK =
        counts.iterator
          .filter { case (_, f) => f >= minFrequency }
          .toVector
          .sortBy { case (w, f) => (-f, w) }
          .take(howMany)

      observer.onStats(Stats(topK))
