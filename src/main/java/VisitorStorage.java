import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;

import static java.lang.System.out;

public class VisitorStorage {
    // Объект для работы с Redis
    private RedissonClient redisson;

    // Объект для работы с ключами
    private RKeys rKeys;

    // Объект для работы с Sorted Set'ом
    private RScoredSortedSet<String> users;

    private RScoredSortedSet<String> savedUsers;

    private final static String KEY = "USERS";


    public RScoredSortedSet<String> getUsers() {
        return users;
    }

    void init() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        try {
            redisson = Redisson.create(config);
        } catch (RedisConnectionException Exc) {
            out.println("Не удалось подключиться к Redis!");
            out.println(Exc.getMessage());
        }
        rKeys = redisson.getKeys();
        users = redisson.getScoredSortedSet(KEY);
        savedUsers = redisson.getScoredSortedSet("SAVEDUSERS");
        rKeys.delete(KEY);
    }

    void shutdown() {
        redisson.shutdown();
    }

    void registerUser(int user_id) {
        users.add(user_id, String.valueOf(user_id));
        savedUsers.add(user_id, String.valueOf(user_id));
    }
}
