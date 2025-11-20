package net.shadowfacts.forgelin.coroutines

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue
import kotlinx.coroutines.CoroutineDispatcher
import net.minecraft.profiler.Profiler
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

internal class ThreadSafeDispatcher {
    private var thread: Thread? = null
    private val logger: Logger = LogManager.getLogger()
    private val tasks: ObjectArrayFIFOQueue<Runnable> = ObjectArrayFIFOQueue()
    private val lock: ReentrantLock = ReentrantLock()

    fun setThread(thread: Thread) {
        this.thread = thread
    }

    fun clear() {
        lock.withLockBusyWait {
            tasks.clear()
        }
        thread = null
    }

    fun needsDispatch(): Boolean {
        return thread !== Thread.currentThread()
    }

    fun runTasks(profiler: Profiler?) {
        profiler?.startSection("jobs")
        try {
            lock.withLockBusyWait {
                while (!tasks.isEmpty) {
                    val task = tasks.dequeue()
                    runCatching {
                        task.run()
                    }.onFailure { e ->
                        logger.error("Error executing task", e)
                    }
                }
            }
        } finally {
            profiler?.endSection()
        }
    }

    fun scheduleTask(job: Runnable) {
        lock.withLockBusyWait {
            tasks.enqueue(job)
        }
    }
}

@PublishedApi
internal object MinecraftServerDispatcher : CoroutineDispatcher() {
    private val taskDispatcher = ThreadSafeDispatcher()
    private var server: MinecraftServer? = null

    @Suppress("unused") //Called from ASM
    @JvmStatic
    fun setup(server: MinecraftServer) {
        this.server = server
        this.taskDispatcher.setThread(Thread.currentThread())
    }

    @Suppress("unused") //Called from ASM
    @JvmStatic
    fun runTasks(profiler: Profiler?) {
        taskDispatcher.runTasks(profiler)
    }

    @Suppress("unused") //Called from ASM
    @JvmStatic
    fun clear() {
        taskDispatcher.clear()
        server = null
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return taskDispatcher.needsDispatch()
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (server?.isServerStopped != false) {
            throw IllegalStateException("Server is not set or not running")
        }
        taskDispatcher.scheduleTask(block)
    }
}

@PublishedApi
internal object MinecraftClientDispatcher: CoroutineDispatcher() {
    private val taskDispatcher = ThreadSafeDispatcher()
    private var isClient: Boolean = false

    @JvmStatic
    fun setup() {
        this.taskDispatcher.setThread(Thread.currentThread())
        isClient = true
    }

    @Suppress("unused") //Called from ASM
    @JvmStatic
    fun runTasks(profiler: Profiler?) {
        taskDispatcher.runTasks(profiler)
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return taskDispatcher.needsDispatch()
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!isClient) {
            throw IllegalStateException("This is either not a minecraft client, or you're dispatching coroutines too early (don't do coroutines during coremod phase please...)")
        }
        taskDispatcher.scheduleTask(block)
    }

}

@OptIn(ExperimentalContracts::class)
private inline fun <T> Lock.withLockBusyWait(action: () -> T): T {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    while (!tryLock()) {
        Thread.yield()
    }
    try {
        return action()
    } finally {
        unlock()
    }
}