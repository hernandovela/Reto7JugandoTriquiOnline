package com.hernandovela.reto7;
import android.content.Context;
import android.media.MediaPlayer;
final class OutcomeFeedback {
 private MediaPlayer player;
 int lastSoundResource;
 static boolean ended(String phase){return "X".equals(phase)||"O".equals(phase)||"draw".equals(phase);}
 void play(Context context,int sound){
  release();lastSoundResource=sound;
  player=MediaPlayer.create(context,sound);
  if(player!=null){player.setOnCompletionListener(p->release());player.setOnErrorListener((p,a,b)->{release();return true;});player.start();}
 }
 void release(){if(player!=null){player.release();player=null;}}
}
