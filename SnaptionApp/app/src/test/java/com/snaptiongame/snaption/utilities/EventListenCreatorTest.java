package com.snaptiongame.snaption.utilities;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.servercalls.EventListenCreator;
import com.snaptiongame.snaption.servercalls.FirebaseReporter;
import com.snaptiongame.snaption.servercalls.ResourceListener;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mock.*;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by austinrobarts on 4/11/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseReporter.class, Log.class})
public class EventListenCreatorTest {

    ResourceListener listener;
    DataSnapshot data;
    DatabaseError error;
    Caption caption;
    Map map;

    @Before
    public void setup() {
        listener = Mockito.mock(ResourceListener.class);
        data = Mockito.mock(DataSnapshot.class);
        error = Mockito.mock(DatabaseError.class);
        caption = Mockito.mock(Caption.class);
        map = Mockito.mock(Map.class);
        PowerMockito.mockStatic(FirebaseReporter.class);
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void testGenericTypeCorrect() {
        Mockito.when(listener.getDataType()).thenReturn(Caption.class);
        GenericTypeIndicator<Map<String, Caption>> genericTypeIndicator =
                new GenericTypeIndicator<Map<String, Caption>>() {};

        Mockito.when(data.getValue(genericTypeIndicator)).thenReturn(map);
        ValueEventListener eventListener = EventListenCreator.getValueEventListener(genericTypeIndicator, listener);
        eventListener.onDataChange(data);
        //test successful onData
        Mockito.verify(data).getValue(genericTypeIndicator);
        Mockito.verify(listener).onData(map);
    }

    @Test
    public void testGenericTypeIncorrect() {
        Mockito.when(listener.getDataType()).thenReturn(Caption.class);
        GenericTypeIndicator<Map<String, Caption>> genericTypeIndicator =
                new GenericTypeIndicator<Map<String, Caption>>() {};

        Mockito.when(data.getValue(genericTypeIndicator)).thenThrow(InputMismatchException.class);
        ValueEventListener eventListener = EventListenCreator.getValueEventListener(genericTypeIndicator, listener);
        eventListener.onDataChange(data);
        //test incorrect onData
        Mockito.verify(data).getValue(genericTypeIndicator);
        Mockito.verify(listener).onData(null);
    }

    @Test
    public void testGenericTypeCancelled() {
        Mockito.when(listener.getDataType()).thenReturn(Caption.class);
        Mockito.when(error.toException()).thenReturn(new DatabaseException("test"));
        GenericTypeIndicator<Map<String, Caption>> genericTypeIndicator =
                new GenericTypeIndicator<Map<String, Caption>>() {};

        ValueEventListener eventListener = EventListenCreator.getValueEventListener(genericTypeIndicator, listener);
        eventListener.onCancelled(error);
        //test canceled event
        Mockito.verify(error).toException();
        Mockito.verify(listener).onData(null);
    }

    @Test
    public void testTypeListenerCorrect() {
        Mockito.when(listener.getDataType()).thenReturn(Caption.class);
        Mockito.when(data.getValue(Caption.class)).thenReturn(caption);

        ValueEventListener eventListener = EventListenCreator.getValueEventListener(Caption.class, listener);
        eventListener.onDataChange(data);

        //test succesful onData event
        Mockito.verify(data).getValue(Caption.class);
        Mockito.verify(listener).onData(caption);
    }

    @Test
    public void testTypeListenerIncorrect() {
        Mockito.when(listener.getDataType()).thenReturn(Caption.class);
        Mockito.when(data.getValue(Caption.class)).thenThrow(InputMismatchException.class);
        ValueEventListener eventListener = EventListenCreator.getValueEventListener(Caption.class, listener);
        eventListener.onDataChange(data);

        //test incorrect onData event
        Mockito.verify(data).getValue(Caption.class);
        Mockito.verify(listener).onData(null);
    }

    @Test
    public void testTypeListenerCancelled() {
        Mockito.when(listener.getDataType()).thenReturn(Caption.class);
        Mockito.when(data.getValue(Caption.class)).thenReturn(caption);
        Mockito.when(error.toException()).thenReturn(new DatabaseException("test"));
        ValueEventListener eventListener = EventListenCreator.getValueEventListener(Caption.class, listener);
        eventListener.onCancelled(error);

        //test canceled event
        Mockito.verify(error).toException();
        Mockito.verify(listener).onData(null);
    }

    @Test
    public void testTypeChildListenerCorrect() {
        Mockito.when(listener.getDataType()).thenReturn(Caption.class);
        Mockito.when(data.getValue(Caption.class)).thenReturn(caption);

        ChildEventListener eventListener = EventListenCreator.getChildEventListener(Caption.class, listener);
        eventListener.onChildAdded(data, "test");

        //test succesful onData event
        Mockito.verify(data).getValue(Caption.class);
        Mockito.verify(listener).onData(caption);
    }

    @Test
    public void testTypeChildListenerIncorrect() {
        Mockito.when(listener.getDataType()).thenReturn(Caption.class);
        Mockito.when(data.getValue(Caption.class)).thenThrow(InputMismatchException.class);
        ChildEventListener eventListener = EventListenCreator.getChildEventListener(Caption.class, listener);
        eventListener.onChildAdded(data, "test");

        //test incorrect onData event
        Mockito.verify(data).getValue(Caption.class);
        Mockito.verify(listener).onData(null);
    }
}
