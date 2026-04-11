package io.github.oni0nfr1.skid.client.internal.schedule

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

internal object Ticker {

    interface TaskHandle {
        /** @return 작업 취소가 성공하면 true, 이미 취소되어 있으면 false */
        fun cancel(): Boolean
        val isCancelled: Boolean
    }

    private data class Task(
        var delay: Int,
        val period: Int?,
        val action: () -> Unit,
        private val cancelled: AtomicBoolean = AtomicBoolean(false)
    ) : TaskHandle {
        override fun cancel(): Boolean = cancelled.compareAndSet(false, true)
        override val isCancelled: Boolean get() = cancelled.get()
    }

    private val logger = LoggerFactory.getLogger("SkidMC_Ticker")
    private val pendingAdd = ConcurrentLinkedQueue<Task>()
    private val tasks = mutableListOf<Task>()
    private val initialized = AtomicBoolean(false)

    fun init() {
        if (!initialized.compareAndSet(false, true)) return
        ClientTickEvents.END_CLIENT_TICK.register { tick() }
    }

    /**
     * 모든 예약된 작업들을 취소하고, 제거합니다. (작동 중인 작업은 중지되지 않습니다.)
     * 월드 이동 또는 서버 연결 해제 시점에 사용하세요.
     */
    fun clear() {
        // cancel everything so external handles reflect cancellation
        for (t in tasks) t.cancel()
        while (true) {
            val t = pendingAdd.poll() ?: break
            t.cancel()
        }
        tasks.clear()
    }

    /**
     * [delay]틱 후에 일회성 작업을 실행합니다.
     * delay=0 이면 이번 틱 종료 시점에 실행하게 됩니다.
     */
    fun runTaskLater(delay: Int, action: () -> Unit): TaskHandle {
        val task = Task(delay.coerceAtLeast(0), null, action)
        pendingAdd.add(task)
        return task
    }

    /**
     * 반복되는 작업을 예약합니다.
     * - [delay]틱 후에 첫 호출이 일어납니다.
     * - 그 뒤부터 [period]틱마다 호출됩니다. period=1이면 매 틱마다 호출됩니다.
     */
    fun runTaskTimer(delay: Int, period: Int, action: () -> Unit): TaskHandle {
        val task = Task(delay.coerceAtLeast(0), period.coerceAtLeast(1), action)
        pendingAdd.add(task)
        return task
    }

    private fun tick() {
        // drain pending queue
        while (true) {
            val t = pendingAdd.poll() ?: break
            if (!t.isCancelled) tasks.add(t)
        }
        if (tasks.isEmpty()) return

        // reverse loop to allow removeAt(i) safely without extra garbage
        var i = tasks.size - 1
        while (i >= 0) {
            val task = tasks[i]

            if (task.isCancelled) {
                tasks.removeAt(i)
                i--
                continue
            }

            if (task.delay > 0) {
                task.delay--
                i--
                continue
            }

            try {
                task.action()
            } catch (t: Throwable) {
                logger.error("Ticker task failed", t)
            }

            val p = task.period
            if (p == null) {
                tasks.removeAt(i)
            } else {
                // period=1 => run every tick
                task.delay = p - 1
            }

            i--
        }
    }
}