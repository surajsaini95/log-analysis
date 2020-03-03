package com.knoldus

import akka.actor.SupervisorStrategy.{Escalate, Resume}
import akka.actor.{ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.AskTimeoutException
import akka.routing.RoundRobinPool
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/** *
 * LogAnalysis is an object whose sole purpose is build actor system and run the scheduler
 */
object LogAnalysis extends App {

  val system = ActorSystem("LogAnalysisActorSystem")

  val props = Props[PrimaryActor]

  /** *
   * the method supervisorStrategy is overridden to provide the custom supervisorStrategy
   *
   * @return SupervisorStrategy
   */
  def mySupervisorStrategy: SupervisorStrategy = {
    val maxNrOfRetries = 5
    val withinTimeRange = 10.second
    OneForOneStrategy(maxNrOfRetries, withinTimeRange) {
      case _: AskTimeoutException => Resume
      case _: Exception => Escalate
    }
  }

  implicit val timeout: Timeout = Timeout(2.second)

  val dirPath = "src/main/resources/log-files"

  val primaryActor = system.actorOf(props, "primaryActor")

  system.scheduler.scheduleWithFixedDelay(1.second, 300.second, primaryActor, ActorMessage(dirPath, "analyse"))

}
