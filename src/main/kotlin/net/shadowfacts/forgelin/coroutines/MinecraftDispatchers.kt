package net.shadowfacts.forgelin.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import net.shadowfacts.forgelin.internal.ServerThreadUtil
import kotlin.coroutines.CoroutineContext

/**
 * The dispatcher that runs the block in the server main thread.
 *
 * The coroutine is invoked immediately if the caller is on the server thread; otherwise, it's invoked in the next tick before the pre-tick phase.
 *
 * Also be aware that if you dispatch coroutines when the server isn't running, it throws [IllegalStateException].
 * For the details when the dispatcher is ready, see [ServerThreadUtil].
 */
@Suppress("UnusedReceiverParameter", "unused") // use the receiver to prevent polluting the top-level functions
val Dispatchers.MinecraftServer: CoroutineDispatcher
    get() = MinecraftServerDispatcher

internal object MinecraftServerDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        ServerThreadUtil.addScheduledTask(block)
    }
}