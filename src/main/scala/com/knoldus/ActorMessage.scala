package com.knoldus

/** *
 * This is a case class used to pass messages by Actors
 *
 * @param path   it contains the directory path
 * @param action specifies what operation should be performed on directory
 */
case class ActorMessage(path: String, action: String)
