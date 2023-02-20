import nl.tgow.datastructures.Stapel;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class StapelTest {

    private Stapel stapel = new Stapel();

    @Test
    public void emptyStackTest(){
        Object result = stapel.pak();
        Assertions.assertNull(result);
    }

    @Test
    public void oneItemInStackTest() {
        stapel.duw("Alice");
        Assertions.assertEquals(1, stapel.lengte());
        Assertions.assertEquals("Alice", stapel.pak());
        Assertions.assertEquals(0, stapel.lengte());
    }

    @Test
    public void multipleItemsInStackTest(){
        stapel.duw("Alice");
        stapel.duw("Bob");
        stapel.duw("Eve");
        Assertions.assertEquals(3, stapel.lengte());
        Assertions.assertEquals("Eve", stapel.pak());
        Assertions.assertEquals("Bob", stapel.pak());
        Assertions.assertEquals(1, stapel.lengte());
    }

    @Test
    public void testDifferentObjectTypes(){
        stapel.duw("Alice");
        stapel.duw(2);
        stapel.duw(23.55);
        stapel.duw(true);

        Assertions.assertEquals(true, stapel.pak());
        Assertions.assertEquals(23.55, stapel.pak());
        Assertions.assertEquals(2, stapel.pak());
        Assertions.assertEquals("Alice", stapel.pak());
    }

}
