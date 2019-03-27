package tk.baumi.ddd;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tk.baumi.test2.Aggregate1;
import tk.baumi.test2.Entity1;
import tk.baumi.test2.Entity2;
import tk.baumi.test2.VO1;

public class TestRepository2 {

	private Repository repoTest;
	@Before
	public void setUp() {
		if (repoTest == null) {
			repoTest = new Repository("jdbc:oracle:thin:@localhost:1521:xe", "afaci", "afaci");
		}
	}
	

	@Test
	public void test1ToNInsert() {
		Entity1 ent1 = new Entity1("ID1", "JOSEFJONAS");
		Entity2 ent2 = new Entity2("ID1", "JOSEFJONAS2");
		VO1 vo1 = new VO1("sdajfkdklhdjkh", "ads;kljdskl;f");
		UUID uuid = UUID.randomUUID();
		Aggregate1 agg1 = new Aggregate1(uuid, ent1, vo1, ent2);
		repoTest.update(agg1);
		
		Aggregate1 aggRet = repoTest.selectByID(Aggregate1.class, uuid);
		assertTrue(agg1.toString().equals(aggRet.toString()));
		repoTest.delete(agg1);
	}
	
	@Test
	public void test1ToNUpdate() {
		Entity1 ent1 = new Entity1("ID2", "JOSEFJONAS");
		Entity2 ent2 = new Entity2("ID2", "JOSEFJONAS2");
		VO1 vo1 = new VO1("sdajfkdklhdjkh", "ads;kljdskl;f");
		UUID uuid = UUID.fromString("a6cfc84a-d2d1-41cf-9adc-f8b4ea69904b");
		Aggregate1 agg1 = new Aggregate1(uuid, ent1, vo1, ent2);
		repoTest.delete(agg1);
		repoTest.update(agg1);
		ent1 = new Entity1("ID2", "JOSEFJONAS3");
		ent2 = new Entity2("ID2", "JOSEFJONAS4");
		vo1 = new VO1("sdjafklfh", "asdjkfldh");
		
		agg1 = new Aggregate1(uuid, ent1, vo1, ent2);
		repoTest.update(agg1);
		
		Aggregate1 aggRet = repoTest.selectByID(Aggregate1.class, uuid);
		assertTrue(agg1.toString().equals(aggRet.toString()));
	}
	@Test
	public void testDelete() {
		UUID testID = UUID.fromString("87dc6e67-7213-4c42-8ffd-447abef04b02");
		Aggregate1 aggregate = new Aggregate1(testID, null, null, null);
		repoTest.update(aggregate);
		Aggregate1 reinitUpdate = repoTest.selectByID(Aggregate1.class, testID);
		assertNotNull(reinitUpdate);
		repoTest.delete(aggregate);
		Aggregate1 reinitDelete = repoTest.selectByID(Aggregate1.class, testID);
		assertNull(reinitDelete);
	}

	@After
	public void closeDown() {
		if (repoTest != null) {
			repoTest.disconnect();
		}
	}
}
