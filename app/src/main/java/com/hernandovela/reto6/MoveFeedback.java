package com.hernandovela.reto7;
final class MoveFeedback {
 // Play only for one confirmed placement. Initial snapshots, resets and repeats are silent.
 static char added(String previous,String next){
  if(previous==null||next==null||previous.length()!=9||next.length()!=9)return ' ';
  int changes=0;char mark=' ';
  for(int i=0;i<9;i++)if(previous.charAt(i)!=next.charAt(i)){
   if(previous.charAt(i)!=' '||(next.charAt(i)!='X'&&next.charAt(i)!='O'))return ' ';
   changes++;mark=next.charAt(i);
  }
  return changes==1?mark:' ';
 }
}
