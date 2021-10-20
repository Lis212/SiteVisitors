import org.redisson.api.RScoredSortedSet;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.out;

public class SiteVisitorsTest {
    // Запуск докер-контейнера:
    // docker run --rm --name skill-redis -p 127.0.0.1:6379:6379/tcp -d redis

    private static final int USERS = 20;

    // Также мы добавим задержку между выводами
    private static final int SLEEP = 1000; // 1 секунда

    private static final SimpleDateFormat DF = new SimpleDateFormat("HH:mm:ss");

    private static void log(String name) {
        String log = String.format("[%s] На главной странице показываем пользователя %s", DF.format(new Date()), name);
        out.println(log);
    }

    public static void main(String[] args) throws InterruptedException {

        VisitorStorage visitorStorage = new VisitorStorage();
        visitorStorage.init();

        for (int i = 1; i <= USERS; i++) {
            visitorStorage.registerUser(i);
        }

        RScoredSortedSet<String> users = visitorStorage.getUsers();

        int i = 1;

        while (true) {
            int randomRichUserId = (int) (Math.random() * users.size());
            String currentUser = users.first();
            log(currentUser);
            users.add(users.lastScore() + 1, currentUser);
            if (i % 10 == 0) {
                users.add(0, String.valueOf(randomRichUserId));
                users.add(users.lastScore() + 1, String.valueOf(randomRichUserId));
                log(users.last());
                i++;
            }
            i++;
            Thread.sleep(SLEEP);
        }
    }
}
