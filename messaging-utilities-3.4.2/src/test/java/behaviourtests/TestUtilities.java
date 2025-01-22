package behaviourtests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.gson.reflect.TypeToken;

import messaging.Event;
import messaging.MessageQueue;

public class TestUtilities {

	protected void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e1) {
		}
	}

	protected void bodyTestDeserialisationGsonRecords(MessageQueue q) throws InterruptedException, ExecutionException {
		CompletableFuture<Person> actual = new CompletableFuture<Person>();
		q.addHandler("list", e -> {
			actual.complete(e.getArgument(0, Person.class));
		});
		Person expected = new Person("some name", 321);
		q.publish(new Event("list", new Object[] { expected }));
		assertEquals(expected, actual.orTimeout(1, TimeUnit.SECONDS).get());
	}
	

	protected void bodyTestDeserialisationOfLists(MessageQueue q) throws InterruptedException, ExecutionException {
		CompletableFuture<List<String>> actual = new CompletableFuture<List<String>>();
		q.addHandler("list", e -> {
			actual.complete(e.getArgument(0, new TypeToken<List<String>>(){}.getType()));
		});
		List<String> expected = new ArrayList<>();
		expected.add("1");
		expected.add("2");
		q.publish(new Event("list", expected));
		actual.join();
		assertEquals(expected,actual.get());
	}
}
