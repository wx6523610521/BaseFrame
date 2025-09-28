package work.chncyl.base.global.redis.message;

@FunctionalInterface
public interface MessageReceiver {
    void receiveMessage(String message, String channel);
}