package de.tu_berlin.dima.experiments.flink.hashagg.flink

import org.apache.flink.api.common.operators.base.ReduceOperatorBase.CombineHint
import org.apache.flink.api.scala._

/** Get the largest value per group based on lexicographic ordering. */
object FlinkLargestLexic {

  def main(args: Array[String]) {
    if (args.length != 3) {
      Console.err.println("Usage: <jar> combiner-strategy inputPath outputPath")
      System.exit(-1)
    }

    val combineHint = args(0).toLowerCase match {
      case "hash" =>
        CombineHint.HASH
      case "sort" =>
        CombineHint.SORT
      case _ =>
        CombineHint.OPTIMIZER_CHOOSES
    }
    val inputPath = args(1)
    val outputPath = args(2)

    val env = ExecutionEnvironment.getExecutionEnvironment

    env
      .readCsvFile[(Long, String)](inputPath)
      .groupBy(0)
      .reduce((x, y) => (x._1, if (x._2 > y._2) x._2 else y._2), combineHint)
      .writeAsCsv(outputPath)

    env.execute("flink-hashagg-largest-lexic")
  }

}
