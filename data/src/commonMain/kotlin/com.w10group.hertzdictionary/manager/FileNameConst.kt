package com.w10group.hertzdictionary.manager

/**
 * 本地保存的文件的文件名，以及直接读取该文件的函数
 * @author Qiao
 */

private const val ABOUT_ME_FILE_NAME = "about_me.txt"
suspend fun readAboutMeFile(): List<String> = readFileToString(ABOUT_ME_FILE_NAME)

private const val FEATURE_FILE_NAME = "feature.txt"
suspend fun readFeatureFile(): List<String> = readFileToString(FEATURE_FILE_NAME)

private const val TECH_FILE_NAME = "technology.txt"
suspend fun readTechFile(): List<KV> = readFileToKV(TECH_FILE_NAME)

private const val OPEN_SOURCE_FILE_NAME = "licence.txt"
suspend fun readOpenSourceFile(): List<KV> = readFileToKV(OPEN_SOURCE_FILE_NAME)