package com.knoldus

import java.io.File

import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class AnalyseLogSpec extends FlatSpec with BeforeAndAfterAll {

  var analyseLogs : AnalyseLogs = _

  override def beforeAll(): Unit = {
    analyseLogs = new AnalyseLogs
  }

  "getListOfFiles method" should "return the list of files in the path specified if it exists" in {
    val actualList = analyseLogs.getListOfFiles("src/main/resources/test-log-files")
    val expectedList = new File("src/main/resources/test-log-files").listFiles.toList
    assert(expectedList == actualList)
  }

  "analyseFile method" should "analyse a single log file" in {
    val file= new File("src/main/resources/test-log-files/log1")
    val actualResult = analyseLogs.analyseFile(file)
    val expectedResult =FileAnalysisResult("log1",8,1446,256)
    assert( expectedResult == actualResult)
  }

  "getAnalysisResult method" should "analyse a list of log file" in {
    val filesList = new File("src/main/resources/test-log-files").listFiles().toList
    val actualResult = analyseLogs.getAnalysisResult(filesList)
    val expectedResult = List(FileAnalysisResult("log2",8,1446,256),FileAnalysisResult("log1",8,1446,256))
    assert(actualResult == expectedResult)
  }


  "getAverageErrorPerFile method" should "return average error per file" in {
    val inputList = List(FileAnalysisResult("log1",8,1446,256),FileAnalysisResult("log2",8,1446,256))
    val actualResult = analyseLogs.getAverageErrorPerFile(inputList)
    val expectedResult = 8
    assert(expectedResult == actualResult)
  }
}
