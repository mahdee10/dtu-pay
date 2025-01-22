package behaviourtests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import messaging.Event;
import messaging.implementations.RabbitMqQueue;

@Disabled("Only works when using RabbitMq")
public class TestMessageQueueNeedingRabbitMq extends TestUtilities {

	@Test
	public void testTopicMatching() {
		var q = new RabbitMqQueue();
		var s = new HashSet<String>();
		q.addHandler("one.*", e -> {
			s.add(e.getTopic());
		});
		q.publish(new Event("one.one"));
		q.publish(new Event("one.two"));
		sleep(100);
		var expected = new HashSet<String>();
		expected.add("one.one");
		expected.add("one.two");
		assertEquals(expected, s);
	}
	
	@Test
	public void testDeserializationOfLists() throws InterruptedException, ExecutionException {
		var q = new RabbitMqQueue();
		bodyTestDeserialisationOfLists(q);
	}
	
	@Test
	public void testGsonDeserializationWithRecordsRabbitMq() throws InterruptedException, ExecutionException {
		var q = new RabbitMqQueue();
		bodyTestDeserialisationGsonRecords(q);
	}
}
