package ch.unige.pinfo.sample;

import org.junit.jupiter.api.Assertions;
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
public class UserServiceTest {

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
        Assertions.assertEquals(messageInQueue + User.findAll().list().size(), eventQueue.received().size());
    }
}
