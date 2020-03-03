package com.knoldus

import java.io.File

import akka.actor.SupervisorStrategy.{Escalate, Resume}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.{AskTimeoutException, ask , pipe}
import akka.routing.RoundRobinPool
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/** *
 * PrimaryActor is an main Actor class on which the actor system will be build
 */
class PrimaryActor extends Actor with ActorLogging {

  /** *
   * the method supervisorStrategy is overridden to provide the custom supervisorStrategy
   *
   * @return SupervisorStrategy
   */
  override def supervisorStrategy: SupervisorStrategy = {
    val maxNrOfRetries = 5
    val withinTimeRange = 10.second
    OneForOneStrategy(maxNrOfRetries, withinTimeRange) {
      case _: AskTimeoutException => Resume
      case _: Exception => Escalate
    }
  }

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
    } else if (d.exists && d.isFile) {
      List.empty[File] :+ d
    } else {
      List[File]()
    }
  }

  /** *
   * the method receive defines which messages the Actor can handle,
   * along with the implementation of how the messages should be processed
   *
   * @return PartialFunction of [Any,Unit]
   */
  override def receive: Receive = {

    case actorMessage: ActorMessage
        if actorMessage.action.equalsIgnoreCase("analyse") =>

              implicit val timeout: Timeout = Timeout(1.second)

              val list = getListOfFiles(actorMessage.path)
              val analysingActor = context.actorOf(Props[SecondaryActor]
                .withRouter(RoundRobinPool(3)), "analysingActor")

              val result = list.map(file => {
                (analysingActor ? file).mapTo[FileAnalysisResult].recover {
                  case askTimeoutException: AskTimeoutException => analysingActor ! askTimeoutException
                    FileAnalysisResult(file.getName, -1, -1, -1)
                }
              })
              val analysisResults = Future.sequence(result)

      val avgErrorCount = analysisResults.map(res => getAvgErrorCount(res)).pipeTo(sender)
      avgErrorCount.map(avgErrCount => log.info(s"\nAnalysis Result\nAverage error count per file is : $avgErrCount"))

  }

  /** *
   * the method getAvgErrorCount can be used to get average error per file
   *
   * @param list takes list of FileAnalysisResult
   * @return average error value
   */
  def getAvgErrorCount(list: List[FileAnalysisResult]): Int = {
    list.foldLeft(0) { (acc, ele: FileAnalysisResult) => acc + ele.errorCount } / list.length
  }
}
