package domain;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is a test class for the Acl domain class
 * 
 * @author Mathias, Frederic, Pieter-Jan
 */
public class AuthTest {
    private static Auth auth;
    private static User user1;
    private static User user2;
    
    public AuthTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        auth = new Auth();
        user1 = new User("John", "admin");
        user2 = new User("Fred", "moderator");
        Auth.registerUser(user1);
        Auth.registerUser(user2);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of login method, of class Auth.
     */
    @Test
    public void testLogin() {
        
        String username = "John";
        
        boolean result = auth.login(username);
        assertEquals(true, result);
        
        // unexisting user
        boolean result2 = auth.login("Kol");
        assertEquals(false, result2);
    }

    /**
     * Test of getUser method, of class Auth.
     */
    @Test
    public void testGetUser() {
        // no user logged in
        assertEquals(null, auth.getUser());
        
        auth.login("John");
        User result = auth.getUser();
        assertEquals(user1, result);
        
        
    }

    /**
     * Test of loggedIn method, of class Auth.
     */
    @Test
    public void testLoggedIn() {
        
        boolean expResult = false;
        boolean result = auth.loggedIn();
        assertEquals(expResult, result);
        
        auth.login("John");
        
        assertEquals(true, auth.loggedIn());
    }

    
    
}