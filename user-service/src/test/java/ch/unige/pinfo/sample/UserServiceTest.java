package ch.unige.pinfo.sample;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import ch.unige.pinfo.sample.model.User;
import ch.unige.pinfo.sample.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@QuarkusTest
class UserServiceTest {

    @Inject
    UserService userService;

    @Inject
    @Any
    InMemoryConnector inMemoryConnector;

    @Test
    void testListAllIds() {
        InMemorySink<User> eventQueue = inMemoryConnector.sink("user-update");
        int messageInQueue = eventQueue.received().size();
        InMemorySource<String> command = inMemoryConnector.source("user-command");
        command.send("get-all");
        Callable<Integer> actualValueSupplier = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventQueue.received().size();
            }
        };

        Awaitility.await().atMost(2, TimeUnit.SECONDS).until(actualValueSupplier, 
                equalTo(messageInQueue + User.findAll().list().size()));

    }
    
    
    @Test
    void testUnknowCommand() {
        InMemorySink<User> eventQueue = inMemoryConnector.sink("user-update");
        int messageInQueue = eventQueue.received().size();
        InMemorySource<String> command = inMemoryConnector.source("user-command");
        command.send("whatever");
        Callable<Integer> actualValueSupplier = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventQueue.received().size();
            }
        };

        Awaitility.await().atMost(2, TimeUnit.SECONDS).until(actualValueSupplier, 
                equalTo(messageInQueue));

    }
    
    
    @Test
    void testDisableUnknowUser() {
        assertThrows(NotFoundException.class, () -> userService.disableUser(666l));

    }

}
