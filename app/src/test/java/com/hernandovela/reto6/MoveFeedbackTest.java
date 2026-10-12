package com.hernandovela.reto7;
import org.junit.Test;
import static org.junit.Assert.*;
public class MoveFeedbackTest {
 @Test public void confirmedMarksHaveDifferentSounds(){assertEquals('X',MoveFeedback.added("         ","X        "));assertEquals('O',MoveFeedback.added("X        ","X   O    "));}
 @Test public void firstLoadDuplicateAndResetAreSilent(){assertEquals(' ',MoveFeedback.added(null,"X        "));assertEquals(' ',MoveFeedback.added("X        ","X        "));assertEquals(' ',MoveFeedback.added("X        ","         "));}
 @Test public void reconnectWithSeveralChangesDoesNotReplayHistory(){assertEquals(' ',MoveFeedback.added("         ","X   O    "));assertEquals(' ',MoveFeedback.added("X        ","O        "));}
}
