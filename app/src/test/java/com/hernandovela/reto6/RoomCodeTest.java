package com.hernandovela.reto7;
import org.junit.Test;
import static org.junit.Assert.*;
public class RoomCodeTest {
 @Test public void pastedCodesAllowSpacesHyphensAndLowercase(){assertEquals("ABCD2345",RoomCode.normalize(" abcd-2345\n"));assertTrue(RoomCode.valid("abcd-2345"));assertEquals("ABCD-2345",RoomCode.display("ABCD2345"));}
 @Test public void invalidCodesCannotBecomePaths(){assertFalse(RoomCode.valid("games/ABCD"));assertFalse(RoomCode.valid("ABCD1234"));assertFalse(RoomCode.valid("ABCDEFG"));assertFalse(RoomCode.valid(null));}
 @Test public void codesUseEightUnambiguousCharacters(){for(int i=0;i<100;i++){String code=RoomCode.generate();assertEquals(8,code.length());assertTrue(RoomCode.valid(code));}}
 @Test public void namesAreRequiredAndBounded(){assertEquals("Ana",RoomCode.player(" Ana "));for(String name:new String[]{"","   ","abcdefghijklmnopqrstuvwxyz"}){try{RoomCode.player(name);fail("Nombre inválido aceptado");}catch(IllegalArgumentException expected){}}}
}
