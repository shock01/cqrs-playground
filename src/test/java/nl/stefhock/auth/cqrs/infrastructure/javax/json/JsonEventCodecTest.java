package nl.stefhock.auth.cqrs.infrastructure.javax.json;

import org.junit.Before;
import org.junit.Test;

public class JsonEventCodecTest {
    private JsonEventCodec classUnderTest;

    @Before
    public void setUp() {
        classUnderTest = new JsonEventCodec();
    }

    @Test
    public void testDecodeDomainEvent() throws Exception {
        byte[] data = new String("{\"eventType\": \"registrationCreated\"}").getBytes();
        System.out.println(classUnderTest.decodeDomainEvent(data));
    }

}