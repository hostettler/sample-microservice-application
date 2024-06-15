package ch.unige.pinfo.sample;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.User;
import ch.unige.pinfo.sample.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

@QuarkusTest
class UserServiceTest {

    @Inject
    UserService userService;

    @Inject
    @Any
    InMemoryConnector inMemoryConnector;

    @Test
    void testListAllIds() throws InterruptedException {
        InMemorySink<User> eventQueue = inMemoryConnector.sink("user-update");
        long messageInQueue = eventQueue.received().size();
        InMemorySource<String> command = inMemoryConnector.source("user-command");
        command.send("get-all");
        Thread.sleep(2000);
        Callable<Integer> actualValueSupplier = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventQueue.received().size();
            }
        };

        Awaitility.await().atMost(2, TimeUnit.SECONDS).until(actualValueSupplier, equalTo(messageInQueue + User.findAll().list().size()));

    }

}
