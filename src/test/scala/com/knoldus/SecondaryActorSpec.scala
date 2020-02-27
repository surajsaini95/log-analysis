package com.knoldus

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers


class SecondaryActorSpec extends TestKit(ActorSystem("SecondaryActorSpec"))
                          with AnyWordSpecLike
                          with Matchers
                          with BeforeAndAfterAll {

  val testingActor = system.actorOf(Props(classOf[SecondaryActor],testActor),"SecondaryActorTesting")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "analyseFile method " must {
    " analyse a single log file" in {
      val file = new File("src/main/resources/log-files/log1")
      /*val actualRes = secondaryActor.analyseFile(file)
      val name: String = "log1"
      val infoCount = 256
      val warningCount = 1446
      val errorCount = 8
      val expectedResult = FileAnalysisResult(name, errorCount, warningCount, infoCount)
      assert(expectedResult == actualRes)*/
    }
  }
}

