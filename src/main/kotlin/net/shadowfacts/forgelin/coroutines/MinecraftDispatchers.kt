package net.shadowfacts.forgelin.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * The dispatcher that runs the block in the server main thread.
 *
 * The coroutine is invoked immediately if the caller is on the server thread; otherwise, it's invoked in the next tick before the pre-tick phase.
 *
 * Also be aware that if you dispatch coroutines when the server isn't running, it throws [IllegalStateException].
 */
@Suppress("UnusedReceiverParameter", "unused") // use the receiver to prevent polluting the top-level functions
val Dispatchers.MinecraftServer: CoroutineDispatcher
    get() = MinecraftServerDispatcher


/**
 * The dispatcher that runs the block in the client main thread.
 *
 * The coroutine is invoked immediately if the caller is on the client thread; otherwise, it's invoked in the next tick before the pre-tick phase.
 *
 * If this is not a client instance, it throws [IllegalStateException]. (Also happens if you're trying to dispatch client thread coroutines during coremod initialization, please don't do that...)
 */
@Suppress("UnusedReceiverParameter", "unused") // use the receiver to prevent polluting the top-level functions
val Dispatchers.MinecraftClient: CoroutineDispatcher
    get() = MinecraftClientDispatcher
