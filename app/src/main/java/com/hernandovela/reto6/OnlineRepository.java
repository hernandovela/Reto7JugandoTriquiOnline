package com.hernandovela.reto7;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;
final class OnlineRepository {
 final FirebaseFirestore db=FirebaseFirestore.getInstance();
 String uid(){return FirebaseAuth.getInstance().getUid();}
 CollectionReference games(){return db.collection("games");}
 Task<DocumentReference> create(String name){
  Map<String,Object> g=new HashMap<>();g.put("host",uid());g.put("guest","");g.put("name",name);g.put("board",Arrays.asList(" "," "," "," "," "," "," "," "," "));g.put("turn","X");g.put("status","waiting");g.put("createdAt",FieldValue.serverTimestamp());return games().add(g);
 }
 Task<Void> change(String id,int cell){
  DocumentReference ref=games().document(id);
  return db.runTransaction(t->{
   DocumentSnapshot g=t.get(ref);
   if(!g.exists())throw new IllegalStateException("La partida no existe.");
   String phase=g.getString("status");
   String mark=uid().equals(g.getString("host"))?"X":uid().equals(g.getString("guest"))?"O":"";
   if(cell==-1){
    if(!"waiting".equals(phase)||!"".equals(mark))throw new IllegalStateException("Partida no disponible.");
    t.update(ref,"guest",uid(),"status","playing");
   }else if(cell==-2){
    if(mark.isEmpty())throw new IllegalStateException("No perteneces a esta partida.");
    if(Arrays.asList("waiting","playing").contains(phase))t.update(ref,"status","closed");
   }else{
    if(mark.isEmpty()||!"playing".equals(phase)||!mark.equals(g.getString("turn")))throw new IllegalStateException("Espera tu turno.");
    TicTacToeGame b=new TicTacToeGame();b.restore(android.text.TextUtils.join("",(List<String>)g.get("board")));
    if(!b.setMove(mark.charAt(0),cell))throw new IllegalStateException("Casilla no disponible.");
    int result=b.result();t.update(ref,"board",Arrays.asList(b.save().split("")),"turn",mark.equals("X")?"O":"X","status",result==0?"playing":result==1?"X":result==2?"O":"draw");
   }
   return null;
  });
 }
}
