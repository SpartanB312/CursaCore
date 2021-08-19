package net.spartanb312.concurrent.task;

import net.spartanb312.concurrent.thread.BlockingContent;

public interface BlockingTask {
    void invoke(BlockingContent unit);
}
