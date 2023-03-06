package com.sturec.sturecteacher.util

import com.google.common.hash.Hashing

class StringHashing {
    fun createHash(inputString:String):String {
        val hashFunction = Hashing.sha256()
        val hc = hashFunction
            .hashString(inputString, Charsets.UTF_8)
//            .newHasher()
//            .putString("The quick brown fox jumps over the lazy dog", Charsets.UTF_8)
//            .hash()
        return hc.toString()
    }
}