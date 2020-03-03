package com.knoldus

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll


class PrimaryActorSpec extends TestKit(ActorSystem("SecondaryActorSpec"))
                          with AnyWordSpecLike
                          with BeforeAndAfterAll
                          with ImplicitSender{

  val testingActor = system.actorOf(Props(classOf[PrimaryActor]),"PrimaryActorTesting")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "analyseFile method " must {
    " analyse a log files and provide the average error count" in {
      within(10.second) {
        val dirPath = "src/main/resources/log-files"
        testingActor ! ActorMessage(dirPath, "analyse")
        val expectedAvgErrorCount = 6
        expectMsg(expectedAvgErrorCount)
      }
    }
  }
}

