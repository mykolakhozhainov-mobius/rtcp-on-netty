package edu.netty.server.task;

import com.mobius.software.common.dal.timers.Task;
import edu.netty.common.message.Message;
import edu.netty.server.channel.ProcessingChannel;

public class MessageProcessingTask implements Task {
    private final Message data;
    private final ProcessingChannel channel;

    public MessageProcessingTask(ProcessingChannel channel, Message msg) {
        super();
        this.data = msg;
        this.channel = channel;
    }
    
    @Override
    public void execute() {
        System.out.println("[TASK] Starting executing " + channel.getChannel().id());
        channel.process(data);
    }

    @Override
    public long getStartTime() {
        return System.currentTimeMillis();
    }

    // Наша ціль - зробити так аби вирішувалася проблема конкурентності
    // Тобто у нас в таймерах є декілька черг
    // Можлива проблема що одна повідомлення для однієї сутності відправляться в різні черги
    // І те що відправилося пізніше виконається швидше
    // Для того аби це вирішити ми робимо наступним чином: для цих повідомлень для однієї сутності у нас є окрема сесія
    // З унікальним ідентифікатором, за допомогою якого ми можемо створити цей ключ
    // В ітозі хешкод повинен бути таким, що для повідомлень однієї сесії відправляє їх в одну і ту саму чергу

    // Дана імплементація це лише заглушка
    public String getId() {
        return "MessageTask-" + System.currentTimeMillis();
    }
}
