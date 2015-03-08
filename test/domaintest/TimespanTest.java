package domaintest;

import java.time.LocalDateTime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import domain.Duration;
import domain.Timespan;
import static org.junit.Assert.*;

/**
 *
 * @author Mathias
 */
public class TimespanTest {
    
    public TimespanTest() {
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

    @Test (expected = IllegalArgumentException.class)
    public void testTimespanConstructorWithEndBeforeBegin() {
        LocalDateTime start = LocalDateTime.of(2015, 1, 6, 14, 30);
        LocalDateTime end = LocalDateTime.of(2015, 1, 6, 14, 29);
        new Timespan(start, end);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testTimespanConstructorWithEqualEndAndBegin() {
        LocalDateTime start = LocalDateTime.of(2015, 1, 3, 14, 30);
        
        new Timespan(start, start);
    }
    
    @Test
    public void testTimespanConstructor() {
        LocalDateTime start = LocalDateTime.of(2015, 1, 6, 14, 29);
        LocalDateTime end = LocalDateTime.of(2015, 1, 6, 14, 30);
        Timespan timespan = new Timespan(start, end);
        
        assertEquals(timespan.getStartTime(), start);
        assertEquals(timespan.getEndTime(), end);
        assertEquals(timespan.getDuration(), new Duration(start, end));
    }
    
    /**
     * Test overlapswith methode of timespan
     */
    @Test
    public void testOverlapsWith() {
        LocalDateTime start1 = LocalDateTime.of(2015, 1, 6, 14, 0);
        LocalDateTime end1 = LocalDateTime.of(2015, 1, 6, 15, 30);
        Timespan timespan1 = new Timespan(start1, end1);
        LocalDateTime start2 = LocalDateTime.of(2015, 1, 6, 14, 30);
        LocalDateTime end2 = LocalDateTime.of(2015, 1, 6, 15, 40);
        Timespan timespan2 = new Timespan(start2, end2);
        
        assertTrue(timespan1.overlapsWith(timespan2));
        // conversely
        assertTrue(timespan2.overlapsWith(timespan1));
        
        // a timespan also overlaps with itself
        assertTrue(timespan1.overlapsWith(timespan1));
        
        LocalDateTime start3 =LocalDateTime.of(2015, 1, 6, 15, 41);
        LocalDateTime end3 = LocalDateTime.of(2015, 1, 6, 15, 52);
        Timespan timespan3 = new Timespan(start3, end3);
        
        assertFalse(timespan2.overlapsWith(timespan3));
        //conversely
        assertFalse(timespan3.overlapsWith(timespan2));
        
    }
    
    /**
     * Test getDelay method of Timespan
     */
     @Test
    public void testGetExcess() {
        LocalDateTime start = LocalDateTime.of(2015, 1, 6, 12, 23);
        LocalDateTime end = LocalDateTime.of(2015, 1, 6, 14, 30);
        Timespan timespan = new Timespan(start, end); // 127 minutes
        Duration dur = new Duration(130);
        assertEquals(0, timespan.getExcess(dur).toMinutes());
        dur = new Duration(100);
        assertEquals(27, timespan.getExcess(dur).toMinutes());
    }
        
    /**
     * Test overlapswith methode of timespan
     */
    @Test
    public void testIsBefore() {
        LocalDateTime start1 = LocalDateTime.of(2015, 1, 6, 14, 0);
        LocalDateTime end1 = LocalDateTime.of(2015, 1, 6, 15, 30);
        Timespan timespan1 = new Timespan(start1, end1);
        LocalDateTime start2 = LocalDateTime.of(2015, 1, 6, 15, 30);
        LocalDateTime end2 = LocalDateTime.of(2015, 1, 6, 15, 40);
        Timespan timespan2 = new Timespan(start2, end2);
        
        assertTrue(timespan1.endsBefore(timespan2));
        // conversely
        assertFalse(timespan2.endsBefore(timespan1));
        
        // a timespan also overlaps with itself
        assertFalse(timespan1.endsBefore(timespan1));
        
        LocalDateTime start3 =LocalDateTime.of(2015, 1, 6, 15, 41);
        LocalDateTime end3 = LocalDateTime.of(2015, 1, 6, 15, 52);
        Timespan timespan3 = new Timespan(start3, end3);
        
        assertTrue(timespan2.endsBefore(timespan3));
       
        
    }
    
    
}
