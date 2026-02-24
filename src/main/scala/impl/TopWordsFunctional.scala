package edu.luc.cs.cs371.topwords
package impl

import scala.collection.immutable.Queue

trait TopWordsEngine:
  def process(words: Iterator[String]): Iterator[Stats]

object TopWordsFunctional:

  final case class Config(
      howMany: Int,
      minLength: Int,
      windowSize: Int,
      caseInsensitive: Boolean,
      everyKSteps: Int,
      minFrequency: Int
  ):
    require(howMany > 0)
    require(minLength > 0)
    require(windowSize > 0)
    require(everyKSteps > 0)
    require(minFrequency > 0)

  private final case class State(
      window: Queue[String],
      counts: Map[String, Int],
      accepted: Int
  )

  def engine(cfg: Config): TopWordsEngine =
    new TopWordsEngine:

      override def process(words: Iterator[String]): Iterator[Stats] =
        val initial = State(Queue.empty, Map.empty, 0)

        words
          .scanLeft((initial, Option.empty[Stats])) { case ((state, _), raw) =>
            step(state, raw, cfg)
          }
          .drop(1)
          .flatMap(_._2)

  private def step(state: State, raw: String, cfg: Config): (State, Option[Stats]) =
    if raw == null then return (state, None)

    val w0 = if cfg.caseInsensitive then raw.toLowerCase.nn else raw
    if w0.length < cfg.minLength then return (state, None)

    val accepted1 = state.accepted + 1

    val window1 = state.window.enqueue(w0)
    val counts1 = state.counts.updated(w0, state.counts.getOrElse(w0, 0) + 1)

    val (window2, counts2) =
      if window1.size > cfg.windowSize then
        val (old, rest) = window1.dequeue
        val newCount = counts1.getOrElse(old, 0) - 1
        val countsAdjusted =
          if newCount <= 0 then counts1 - old
          else counts1.updated(old, newCount)
        (rest, countsAdjusted)
      else (window1, counts1)

    val state2 = State(window2, counts2, accepted1)

    val emit =
      window2.size == cfg.windowSize && accepted1 % cfg.everyKSteps == 0

    if !emit then (state2, None)
    else
      val topK =
        counts2.iterator
          .filter { case (_, f) => f >= cfg.minFrequency }
          .toVector
          .sortBy { case (word, freq) => (-freq, word) }
          .take(cfg.howMany)

      (state2, Some(Stats(topK)))