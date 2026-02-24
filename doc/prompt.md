 Role: You are a Scala 3 expert. 

 Task: Generate a rough draft for a streaming word-count project called `topwords` using the following file structure and requirements. Use Scala 3 syntax (significant indentation, no curly braces where possible).

 File Structure & Logic:
 1. `impl/Stat.scala`: A case class `Stat(word: String, frequency: Int)`.
 2. `impl/Observer.scala`: 
    - A trait `Observer` with a method `onUpdate(stats: Seq[Stat]): Unit`.
    - A class `ConsoleObserver` that implements `Observer`. It must print stats in the format `w1: f1 w2: f2` and handle `java.io.IOException` (SIGPIPE) by calling `System.exit(0)`.
 3. `impl/TopWords.scala`: 
    - A class `TopWords` taking `cloudSize`, `minLength`, `windowSize`, and an `Observer`.
    - Use a `mutable.Queue` for a sliding window and a `mutable.Map` for counts to ensure $O(windowSize)$ space complexity.
    - Method `processWord(word: String)`: Update the window/counts. If `window.size >= windowSize`, sort by frequency (desc) then alphabetically, and notify the observer.
 4. `main/Main.scala`: 
    - Use the `mainargs` library to parse arguments: `--cloud-size` (default 10), `--length-at-least` (default 6), and `--window-size` (default 1000).
   - Read `scala.io.Source.stdin`, split into words using the regex `(?U)[^\\p{Alpha}0-9']+`, and feed them into the `TopWords` engine.

 Constraints: > - Ensure the engine does not print anything for the first $n-1$ words.
 - Maintain "Scala as a better Java" style: use `val` where possible, but `mutable` collections for performance/state within the engine.