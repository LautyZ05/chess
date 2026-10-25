package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService service;

    @BeforeEach
    public void setup(){
        service = new UserService(new MemoryUserDao(), new MemoryAuthDao());
    }

    @Test
    void registerSuccess() throws Exception{
        var auth = service.register(new UserData("test1", "password1", "test@mail.com"));
        assertEquals("test1", auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    void loginSuccess() throws Exception{
        service.register(new UserData("test1", "password1", "test@mail.com"));
        var auth = service.login(new UserService.LoginRequest("test1", "password1"));
        assertEquals("test1", auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    void loginWrongPassword() throws Exception{
        service.register(new UserData("test1", "password1", "test@mail.com"));
        var e = assertThrows(ServiceException.class, () -> service.login(new UserService.LoginRequest("test1", "password2")));
        assertEquals(401, e.status());
    }

    @Test
    void loginUnknownUser(){
        var e = assertThrows(ServiceException.class, () -> service.login(new UserService.LoginRequest("random1", "ran_pass1")));
        assertEquals(401, e.status());
    }

    @Test
    void logoutSuccess() throws Exception{
        var auth = service.register(new UserData("test1", "password1", "test@mail.com"));
        service.logout(auth.authToken());

        var e = assertThrows(ServiceException.class, () -> service.logout(auth.authToken()));
        assertEquals(401, e.status());
    }
}