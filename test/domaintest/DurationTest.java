package domaintest;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import domain.Duration;
import static org.junit.Assert.*;

/**
 *
 * @author Mathias
 */
public class DurationTest {
    
    public DurationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Test of the Duration constructor between 2 times
     */
    @Test
    public void testDurationBetweenConstructor() {
        
        // different days, same week
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 12, 30);        
        LocalDateTime end = LocalDateTime.of(2015, 2, 20, 9, 30);
        Duration duration = new Duration(begin, end);
        
        assertEquals(29, duration.getHours());
        assertEquals(0, duration.getMinutes());
        
        // different days, different weeks
        LocalDateTime begin2 = LocalDateTime.of(2015, 2, 16, 12, 30);        
        LocalDateTime end2 = LocalDateTime.of(2015, 2, 25, 9, 35);
        Duration duration2 = new Duration(begin2, end2);
        
        assertEquals(53, duration2.getHours());
        assertEquals(5, duration2.getMinutes());
        
         // same day
        LocalDateTime begin3 = LocalDateTime.of(2015, 2, 16, 12, 30);        
        LocalDateTime end3 = LocalDateTime.of(2015, 2, 16, 15, 19);
        Duration duration3 = new Duration(begin3, end3);
        
        assertEquals(2, duration3.getHours());
        assertEquals(49, duration3.getMinutes());
    }
    
    /**
     * Test of the Duration constructor between 2 times, with an end time the same
     * as begin.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testDurationBetweenConstructorInvalidInterval() {
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 12, 30);        
        Duration duration = new Duration(begin, begin);
    }
    
    /**
     * Test of the Duration constructor between 2 times, with a begin time before
     * the bussiness hours
     */
    @Test (expected = IllegalArgumentException.class)
    public void testDurationBetweenConstructorInvalidBeginTime() {
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 6, 0);        
        LocalDateTime end = LocalDateTime.of(2015, 2, 20, 9, 30);
        Duration duration = new Duration(begin, end);
    }
    
    /**
     * Test of the Duration constructor between 2 times, with an end time after
     * the bussiness hours
     */
    @Test (expected = IllegalArgumentException.class)
    public void testDurationBetweenConstructorInvalidEndTime() {
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 8, 0);        
        LocalDateTime end = LocalDateTime.of(2015, 2, 20, 18, 30);
        Duration duration = new Duration(begin, end);
    }
    


    
   /**
     * Test of add method, of class Duration.
     */
    @Test
    public void testAddMinutes() {
        Duration instance = new Duration(100);
        instance = instance.add(150);
        assertEquals(250, instance.toMinutes());
        instance = instance.add(-150);
        assertEquals(100, instance.toMinutes());
    }
    
    /**
     * Test of add method with negative amount of minutes that will cause the
     * Duration to be negative.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testAddMinutesNegative() {
        Duration instance = new Duration(100);
        instance = instance.add(-150);
       
    }
    
    /**
     * Test of add method with another duration, of class Duration.
     */
    @Test
    public void testAddDuration() {
        Duration instance = new Duration(100);
        Duration instance2 = new Duration(100);
        instance = instance.add(instance2);
        assertEquals(instance.toMinutes(), 200);
    }
    
    /**
     * Test of add method with another duration, of class Duration.
     */
    @Test
    public void testSubtractDuration() {
        Duration instance = new Duration(100);
        Duration instance2 = new Duration(100);
        instance = instance.subtract(instance2);
        assertEquals(instance.toMinutes(), 0);
    }
    
    /**
     * Test of subtract method with another duration, which causes the duration
     * to be negative.
     */
    @Test (expected = IllegalArgumentException.class)
    public void testSubtractDurationNegative() {
        Duration instance = new Duration(100);
        Duration instance2 = new Duration(300);
        instance = instance.subtract(instance2);
        
    }

    /**
     * Test of getEndTimeFrom method, of class Duration.
     */
    @Test
    public void testGetEndTimeFrom() {
        // different days
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 8, 0);
        Duration instance = new Duration(1940);
        LocalDateTime result = instance.getEndTimeFrom(begin);
        assertEquals(LocalDateTime.of(2015, 2, 18, 8, 20), result);
        
        // same day
        
        Duration instance2 = new Duration(125);
        result = instance2.getEndTimeFrom(begin);
        assertEquals(LocalDateTime.of(2015, 2, 16, 10, 5), result);
        
    }

    
    /**
     * Test of multiplyBy method, of class Duration.
     */
    @Test
    public void testMultiplyBy() {
        // different days
        
        Duration instance = new Duration(1941);
        
        assertEquals(1165, instance.multiplyBy(0.6).toMinutes());
        
            
    }
    
}
