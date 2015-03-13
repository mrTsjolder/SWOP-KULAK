package domain;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * This unit class tests the duration class.
 * 
 * @author Frederic, Mathias, Pieter-Jan
 */
public class DurationTest {

    /**
     * Test of the Duration constructor between 2 times
     */
    @Test
    public void testDurationBetweenConstructor() {

        // different days, same week, lunch break on first day and last day
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 10, 00);
        LocalDateTime end = LocalDateTime.of(2015, 2, 20, 16, 30);
        Duration duration = new Duration(begin, end);

        assertEquals(37, duration.getHours());
        assertEquals(30, duration.getMinutes());

        // different days, different weeks
        LocalDateTime begin2 = LocalDateTime.of(2015, 2, 16, 14, 30);
        LocalDateTime end2 = LocalDateTime.of(2015, 2, 25, 9, 35);
        Duration duration2 = new Duration(begin2, end2);

        assertEquals(52, duration2.getHours());
        assertEquals(5, duration2.getMinutes());

        // same day including lunchbreak
        LocalDateTime begin3 = LocalDateTime.of(2015, 2, 16, 11, 30);
        LocalDateTime end3 = LocalDateTime.of(2015, 2, 16, 15, 19);
        Duration duration3 = new Duration(begin3, end3);

        assertEquals(2, duration3.getHours());
        assertEquals(49, duration3.getMinutes());
    }

    /**
     * Test of the Duration constructor between 2 times, with an end time the
     * same as begin.
     */
    @Test
    public void testDurationBetweenConstructorEmptyInterval() {
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 12, 30);
        Duration d = new Duration(begin, begin);
        assertTrue(d.equals(Duration.ZERO));
    }

    /**
     * Test of the Duration constructor between 2 times, with a begin time
     * before the bussiness hours
     */
    @Test
    public void testDurationBetweenConstructorInvalidBeginTime() {
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 6, 0);
        LocalDateTime end = LocalDateTime.of(2015, 2, 20, 9, 30);
        Duration d = new Duration(begin, end);
        assertEquals(32, d.getHours());
        assertEquals(30, d.getMinutes());
    }
    
    /**
     * Test of the Duration constructor between 2 times, with a begin time
     * after the end time
     */
    @Test (expected = IllegalArgumentException.class)
    public void testDurationBetweenConstructorInvalidInterval() {
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 10, 0);
        LocalDateTime end = LocalDateTime.of(2015, 2, 15, 9, 30);
        new Duration(begin, end);
        
    }

    /**
     * Test of the Duration constructor between 2 times, with an end time after
     * the bussiness hours
     */
    @Test
    public void testDurationBetweenConstructorInvalidEndTime() {
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 9, 0);
        LocalDateTime end = LocalDateTime.of(2015, 2, 20, 18, 30);
        Duration d = new Duration(begin, end);
        assertEquals(40, d.getHours());
        assertEquals(0, d.getMinutes());
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
        assertEquals(instance, instance.add(null));
    }

    /**
     * Test of add method with negative amount of minutes that will cause the
     * Duration to be negative.
     */
    @Test(expected = IllegalArgumentException.class)
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
        assertEquals(instance2, instance2.subtract(null));
    }

    /**
     * Test of subtract method with another duration, which causes the duration
     * to be negative.
     */
    @Test(expected = IllegalArgumentException.class)
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
        LocalDateTime begin = LocalDateTime.of(2015, 2, 16, 9, 0);
        Duration instance = new Duration(1940);
        LocalDateTime result = instance.getEndTimeFrom(begin);
        assertEquals(LocalDateTime.of(2015, 2, 20, 9, 20), result);

        // same day
        Duration instance2 = new Duration(125);
        result = instance2.getEndTimeFrom(begin);
        assertEquals(LocalDateTime.of(2015, 2, 16, 11, 5), result);

        // different days with lunchbreak in last day
        LocalDateTime begin3 = LocalDateTime.of(2015, 2, 16, 12, 0);
//        Duration instance3 = new Duration(1940);
        LocalDateTime result3 = instance.getEndTimeFrom(begin3);
        assertEquals(LocalDateTime.of(2015, 2, 20, 13, 20), result3);

        // different with multiple weekends
        LocalDateTime begin4 = LocalDateTime.of(2015, 2, 12, 12, 0);
        Duration instance4 = new Duration(4800);
        LocalDateTime result4 = instance4.getEndTimeFrom(begin4);
        assertEquals(LocalDateTime.of(2015, 2, 26, 12, 0), result4);

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
    
     /**
     * Test of percentage over method, of class Duration.
     */
    @Test
    public void testPercentageOver() {
       
        Duration instance = new Duration(1500);
        Duration instance2 = new Duration(3000);
        assertEquals(0, instance.percentageOver(instance2),0);
        assertEquals(1, instance2.percentageOver(instance),0);
    }

    /**
     * Test nextValidWorkTime method in case of a valid work time.
     */
    @Test
    public void testNextValidWorkTimeValid() {
        LocalDateTime worktime = LocalDateTime.of(2015, 3, 10, 10, 0);
        assertTrue(Duration.isValidWorkTime(worktime));
        assertEquals(worktime, Duration.nextValidWorkTime(worktime));
    }

    /**
     * Test nextValidWorkTime method in case of a time before the work day
     * began.
     */
    @Test
    public void testNextValidWorkTimeTooEarly() {
        LocalDateTime tooEarly = LocalDateTime.of(2015, 3, 10, 5, 0);
        assertTrue(Duration.isValidWorkDay(tooEarly));
        assertTrue(tooEarly.toLocalTime().isBefore(Duration.BEGINWORKDAY));
        assertEquals(LocalDateTime.of(tooEarly.toLocalDate(), Duration.BEGINWORKDAY),
                Duration.nextValidWorkTime(tooEarly));
    }
    
    /**
     * Test of equals method, of class Duration.
     */
    @Test
    public void testEquals() {
        
        Duration instance = new Duration(1941);
        Duration instance2 = new Duration(1941);
        Duration instance3 = new Duration(2);
        assertTrue(instance.equals(instance2));
        assertTrue(instance2.equals(instance));
        assertFalse(instance.equals(instance3));
        assertFalse(instance.equals("Not a duration"));
        
    }

    /**
     * Test nextValidWorkTime method in case of a time after the work day ended.
     */
    @Test
    public void testNextValidWorkTimeTooLate() {
        LocalDateTime tooLate = LocalDateTime.of(2015, 3, 10, 22, 0);
        assertTrue(Duration.isValidWorkDay(tooLate));
        assertTrue(tooLate.getDayOfWeek().getValue() != Duration.ENDWORKWEEK);
        assertTrue(tooLate.toLocalTime().isAfter(Duration.ENDWORKDAY));
        assertEquals(LocalDateTime.of(tooLate.toLocalDate().plusDays(1), Duration.BEGINWORKDAY),
                Duration.nextValidWorkTime(tooLate));
    }

    /**
     * Test nextValidWorkTime method in case of a time during lunch break.
     */
    @Test
    public void testNextValidWorkTimeLunch() {
        LocalDateTime lunch = LocalDateTime.of(2015, 3, 10, 12, 30);
        assertTrue(Duration.isValidWorkDay(lunch));
        assertTrue(lunch.toLocalTime().isAfter(Duration.BEGINLUNCH));
        assertTrue(lunch.toLocalTime().isBefore(Duration.ENDLUNCH));
        assertEquals(LocalDateTime.of(lunch.toLocalDate(), Duration.ENDLUNCH),
                Duration.nextValidWorkTime(lunch));
    }

    /**
     * Test nextValidWorkTime method in case of a not working time.
     */
    @Test
    public void testNextValidWorkTimeWeekend() {
        LocalDateTime saturdayMorning = LocalDateTime.of(2015, 3, 7, 4, 0);
        assertFalse(Duration.isValidWorkDay(saturdayMorning));
        assertFalse(Duration.isValidWorkDay(saturdayMorning.plusDays(1)));
        assertTrue(Duration.isValidWorkDay(saturdayMorning.plusDays(2)));
        assertEquals(LocalDateTime.of(saturdayMorning.toLocalDate().plusDays(2), Duration.BEGINWORKDAY),
                Duration.nextValidWorkTime(saturdayMorning));

        LocalDateTime saturdayEvening = LocalDateTime.of(2015, 3, 7, 1, 0);
        assertFalse(Duration.isValidWorkDay(saturdayEvening));
        assertFalse(Duration.isValidWorkDay(saturdayEvening.plusDays(1)));
        assertTrue(Duration.isValidWorkDay(saturdayEvening.plusDays(2)));
        assertEquals(LocalDateTime.of(saturdayEvening.toLocalDate().plusDays(2), Duration.BEGINWORKDAY),
                Duration.nextValidWorkTime(saturdayEvening));

        LocalDateTime sundayMorning = LocalDateTime.of(2015, 3, 8, 10, 30);
        assertFalse(Duration.isValidWorkDay(sundayMorning));
        assertTrue(Duration.isValidWorkDay(sundayMorning.plusDays(1)));
        assertEquals(LocalDateTime.of(sundayMorning.toLocalDate().plusDays(1), Duration.BEGINWORKDAY),
                Duration.nextValidWorkTime(sundayMorning));

        LocalDateTime sundayEvening = LocalDateTime.of(2015, 3, 8, 22, 45);
        assertFalse(Duration.isValidWorkDay(sundayEvening));
        assertTrue(Duration.isValidWorkDay(sundayEvening.plusDays(1)));
        assertEquals(LocalDateTime.of(sundayEvening.toLocalDate().plusDays(1), Duration.BEGINWORKDAY),
                Duration.nextValidWorkTime(sundayEvening));
    }

}
