package com.example.b07project;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PresenterTest extends TestCase {

    @Mock
    DisplayLogin view;

    @Mock
    Model model;

    @Mock
    User u;

    @Test
    public void testCheckPassword(){
        when(view.getUsername()).thenReturn("abc");
        when(view.getPassword()).thenReturn("123");
        when(model.authenticateUser("abc", "123")).thenReturn(true);

        //testCheckPassword()
        Presenter presenter = new Presenter(model, view);
        presenter.checkPassword();
        verify(view).hideMessage();
        verify(view).logIn();

        //testCreateUser()
        assertFalse(presenter.createUser(u));
        verify(view).hideMessage();
        verify(view).logIn();
    }


}