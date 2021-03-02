package com.pubnub.framework.mapper

/**
 * Default mapper interface
 */
interface Mapper<in Input, out Output> {
    fun map(input: Input): Output
}
