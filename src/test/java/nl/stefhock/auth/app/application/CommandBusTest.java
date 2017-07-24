package nl.stefhock.auth.app.application;

import nl.stefhock.auth.cqrs.application.Command;
import nl.stefhock.auth.cqrs.application.CommandBus;
import nl.stefhock.auth.cqrs.application.CommandHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by hocks on 5-7-2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommandBusTest {
    private CommandBus classUnderTest;

    @Mock
    private CommandHandler<TestCommand> handler;
    private TestCommand command;

    @Before
    public void setUp() throws Exception {
        command = new TestCommand();
        classUnderTest = new CommandBus();
    }

    @Test
    public void register() throws Exception {
        classUnderTest.register(TestCommand.class, handler);
        assertTrue(classUnderTest.hasHandler(TestCommand.class));
    }

    @Test(expected = RuntimeException.class)
    public void executeWithoutHandlerShouldThrow() throws Exception {
        classUnderTest.execute(command);

    }

    @Test
    public void execute() throws Exception {
        classUnderTest.register(TestCommand.class, handler);
        classUnderTest.execute(command);
        verify(handler, times(1)).execute(eq(command));
    }

    static class TestCommand extends Command {
    }

}