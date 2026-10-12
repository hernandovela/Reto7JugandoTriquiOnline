package com.hernandovela.reto7;
import java.security.SecureRandom;
import java.util.Locale;
final class RoomCode {
 static final String ALPHABET="ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
 private static final SecureRandom RANDOM=new SecureRandom();
 static String generate(){StringBuilder code=new StringBuilder();for(int i=0;i<8;i++)code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));return code.toString();}
 static String normalize(String value){return value==null?"":value.toUpperCase(Locale.ROOT).replaceAll("[\\s-]","");}
 static boolean valid(String value){return normalize(value).matches("[A-HJ-NP-Z2-9]{8}");}
 static String display(String value){return valid(value)?normalize(value).substring(0,4)+"-"+normalize(value).substring(4):value;}
 static String player(String value){String name=value==null?"":value.trim();if(name.isEmpty()||name.length()>24)throw new IllegalArgumentException("Escribe tu nombre (1 a 24 caracteres).");return name;}
}
