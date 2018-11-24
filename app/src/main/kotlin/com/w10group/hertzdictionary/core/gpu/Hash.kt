package com.w10group.hertzdictionary.core.gpu

import java.security.MessageDigest

/**
 * Hash算法
 */

fun sha256Hash(str: String): String = getHashAlgorithm(SHA_256, str)

fun sha512Hash(str: String): String = getHashAlgorithm(SHA_512, str)

fun md5Hash(str: String): String = getHashAlgorithm(MD5, str)

private fun getHashAlgorithm(key: String, str: String): String {
    val mDigest = MessageDigest.getInstance(key)
    mDigest.update(str.toByteArray())
    return bytesToHexString(mDigest.digest())
}

private fun bytesToHexString(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (b in bytes) {
        val hex = Integer.toHexString(0xFF and b.toInt())
        if (hex.length == 1) {
            sb.append('0')
        }
        sb.append(hex)
    }
    return sb.toString()
}

const val MD5 = "MD5"
const val SHA_256 = "SHA-256"
const val SHA_512 = "SHA-512"