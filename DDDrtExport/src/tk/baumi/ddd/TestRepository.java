package tk.baumi.ddd;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tk.baumi.test.Aggregate1;
import tk.baumi.test.Entity1;
import tk.baumi.test.Entity2;
import tk.baumi.test.VO1;

public class TestRepository {
	private Repository repoTest;

	@Before
	public void setUp() {
		if (repoTest == null) {
			repoTest = new Repository("jdbc:oracle:thin:@localhost:1521:xe", "afaci", "afaci");
		}
	}
	
	@Test
	public void testDelete() {
		String testID = "testdelete";
		Aggregate1 aggregate = new Aggregate1(testID, null);
		repoTest.update(aggregate);
		Aggregate1 reinitUpdate = repoTest.selectByID(Aggregate1.class, testID);
		assertNotNull(reinitUpdate);
		repoTest.delete(aggregate);
		Aggregate1 reinitDelete = repoTest.selectByID(Aggregate1.class, testID);
		assertNull(reinitDelete);
	}
	
	public void test1ToNInsert() {
		tk.baumi.test2.Entity1 ent1 = new tk.baumi.test2.Entity1("ID1", "JOSEFJONAS");
		tk.baumi.test2.Entity2 ent2 = new tk.baumi.test2.Entity2("ID1", "JOSEFJONAS2");
		tk.baumi.test2.Aggregate1 agg1 = new tk.baumi.test2.Aggregate1();
	}
	
	@Test
	public void testMToNInsert() {
		List<VO1> vos = new LinkedList<>();
		vos.add(new VO1(46494984354564l, 95.69));
		vos.add(new VO1(56449846421231l, 12869.339));

		List<VO1> vos2 = new LinkedList<>();
		vos2.add(new VO1(4649456456231l, 5654.131));
		vos2.add(new VO1(564445415648156l, 64564.114));

		List<Entity2> ent2s = new LinkedList<>();
		ent2s.add(new Entity2("10000001", vos));
		ent2s.add(new Entity2("10000002", vos2));
		ent2s.add(new Entity2("10000003", null));

		List<Entity1> ent1s = new LinkedList<>();
		ent1s.add(new Entity1("ID1", ent2s, new Date(System.currentTimeMillis())));
		ent1s.add(new Entity1("ID2", null, new Date(System.currentTimeMillis() - 5)));
		String aggregateID = "8858515641564562313";
		Aggregate1 agg = new Aggregate1(aggregateID, ent1s);
		repoTest.delete(agg);
		repoTest.update(agg);

		Aggregate1 ent1r = repoTest.selectByID(Aggregate1.class, aggregateID);
		assertTrue(agg.toString().equals(ent1r.toString()));

		Entity1 ent1 = new Entity1();
		ent1s.add(ent1);
		assertFalse(agg.toString().equals(ent1r.toString()));

		ent1s.remove(ent1);
		assertTrue(agg.toString().equals(ent1r.toString()));
	}

	@Test
	public void testMToNUpdate() {
		List<VO1> vos = new LinkedList<>();
		vos.add(new VO1(46494984354564l, 95.69));
		vos.add(new VO1(56449846421231l, 12869.339));

		List<VO1> vos2 = new LinkedList<>();
		vos2.add(new VO1(4649456456231l, 5654.131));
		vos2.add(new VO1(564445415648156l, 64564.114));

		List<Entity2> ent2s = new LinkedList<>();
		ent2s.add(new Entity2("10000001", vos));
		ent2s.add(new Entity2("10000002", vos2));
		ent2s.add(new Entity2("10000003", null));

		List<Entity1> ent1s = new LinkedList<>();
		ent1s.add(new Entity1("ID1", ent2s, new Date(System.currentTimeMillis())));
		ent1s.add(new Entity1("ID2", null, new Date(System.currentTimeMillis() - 5)));
		String aggregateID = "8858515641564562313";
		Aggregate1 agg = new Aggregate1(aggregateID, ent1s);
		repoTest.update(agg);

		Aggregate1 ent1r = repoTest.selectByID(Aggregate1.class, aggregateID);
		assertTrue(agg.toString().equals(ent1r.toString()));

		Entity1 ent1 = new Entity1();
		ent1s.add(ent1);
		assertFalse(agg.toString().equals(ent1r.toString()));

		ent1s.remove(ent1);
		assertTrue(agg.toString().equals(ent1r.toString()));
	}

	@After
	public void closeDown() {
		if (repoTest != null) {
			repoTest.disconnect();
		}
	}

}
