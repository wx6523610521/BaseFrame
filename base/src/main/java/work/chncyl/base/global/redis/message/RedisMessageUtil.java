package work.chncyl.base.global.redis.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConnectionFactory connectionFactory;

    /**
     * 发布消息
     *
     * @param channels 发布的频道
     * @param content  发布的内容
     */
    public void sendMessage(String content, String... channels) {
        for (String channel : channels) {
            redisTemplate.convertAndSend(channel, content);
        }
    }

    /**
     * 订阅频道
     *
     * @param receiver 消息接收器
     * @param channels 订阅的频道
     * @return 订阅结果容器, 可执行removeListener/remove方法去除监听器
     */
    public RedisMessageListenerContainer container(work.chncyl.base.global.redis.message.MessageReceiver receiver, String... channels) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 使用处理器，在接受到消息时调用方法
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveMessage");
        // 订阅频道
        for (String s : channels) {
            container.addMessageListener(adapter, new ChannelTopic(s));
        }
        return container;
    }
}