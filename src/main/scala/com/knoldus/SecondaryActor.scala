package com.knoldus

import java.io.File

import akka.actor.{Actor, ActorLogging}
import akka.pattern.AskTimeoutException
import scala.io.Source

/** *
 * SecondaryActor is an  Actor class whose actors will be spawned and used by PrimaryActor
 */
class SecondaryActor extends Actor with ActorLogging {

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
   * the method receive defines which messages the Actor can handle,
   * along with the implementation of how the messages should be processed
   *
   * @return PartialFunction of [Any,Unit]
   */
  override def receive: Receive = {
    case file: File => log.info("passed : " + self.path)
      sender ! analyseFile(file)

    case askTimeoutException: AskTimeoutException => log.info("failed : " + self.path)
      throw askTimeoutException

    case _: Exception => log.info("exception : " + self.path)
      throw new Exception("no such msg defined for secondaryActor")

  }
}
