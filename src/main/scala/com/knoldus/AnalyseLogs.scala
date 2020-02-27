package com.knoldus

import java.io.File

import scala.io.Source

/** *
 * This class can be used to analyse log files based on error,warning and info count
 */
class AnalyseLogs {

  /** *
   * the method getListOfFiles can be used to get the list of all files in the given directory
   *
   * @param inputDirectory contains the path of directory as input
   * @return list of files as output
   */
  def getListOfFiles(inputDirectory: String): List[File] = {
    val d = new File(inputDirectory)
    if (d.exists && d.isDirectory) {
      d.listFiles.toList
    } else {
      List[File]()
    }
  }

  /** *
   * the method analyseFile can be used to analyse a single log file
   *
   * @param file takes a single file as an input
   * @return analysis result in the format of case class FileAnalysisResult
   */
  def analyseFile(file: File): FileAnalysisResult = {
    val fileContent = Source.fromFile(file).getLines.toList
    val res = fileContent.foldLeft((0, 0, 0)) { (acc, line) => {
      if (line.contains("ERROR")) {
        (acc._1 + 1, acc._2, acc._3)
      }
      else if (line.contains("WARN")) {
        (acc._1, acc._2 + 1, acc._3)
      }
      else if (line.contains("INFO")) {
        (acc._1, acc._2, acc._3 + 1)
      }
      else {
        acc
      }
    }
    }
    FileAnalysisResult(file.getName, res._1, res._2, res._3)
  }

  /** *
   * the method getAnalysisResult can be used to analyse list of log files
   *
   * @param filesList takes a list of file as an input
   * @return analysis result in the format of list of case class FileAnalysisResult
   */
  def getAnalysisResult(filesList: List[File]): List[FileAnalysisResult] = {
    filesList.map(file => analyseFile(file))
  }

  /** *
   * the method getAverageErrorPerFile can be used to get average error per file
   *
   * @param analysisResult takes list of FileAnalysisResult
   * @return average error value
   */
  def getAverageErrorPerFile(analysisResult: List[FileAnalysisResult]): Int = {
    analysisResult.foldLeft(0) { (errorSum, log) => errorSum + log.errorCount } / analysisResult.length
  }
}


