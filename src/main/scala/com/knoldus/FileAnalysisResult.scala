package com.knoldus

/** *
 * This is a case class to record the analysis of single log file
 *
 * @param name         represents name of the log file
 * @param errorCount   represents errors in the log file
 * @param warningCount represents warnings in the log file
 * @param infoCount    represents info in the log file
 */
case class FileAnalysisResult(name: String, errorCount: Int, warningCount: Int, infoCount: Int)
