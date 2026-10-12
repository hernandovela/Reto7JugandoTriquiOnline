package com.hernandovela.reto7;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;
final class OnlineRepository {
 final FirebaseFirestore db;
 final FirebaseAuth auth;
 OnlineRepository(){this(com.google.firebase.FirebaseApp.getInstance());}
 OnlineRepository(com.google.firebase.FirebaseApp app){db=FirebaseFirestore.getInstance(app);auth=FirebaseAuth.getInstance(app);}
 String uid(){return auth.getUid();}
 CollectionReference games(){return db.collection("games_v2");}
 Task<DocumentReference> create(String name,String player){
  final String hostName=RoomCode.player(player),code=RoomCode.generate();
  final DocumentReference ref=games().document(code);
  return db.runTransaction(t->{
   if(t.get(ref).exists())throw new IllegalStateException("El código ya existe. Vuelve a crear la partida.");
   Map<String,Object> g=new HashMap<>();
   g.put("host",uid());g.put("guest","");g.put("hostName",hostName);g.put("guestName","");
   g.put("name",name);g.put("code",code);
   g.put("board",Arrays.asList(" "," "," "," "," "," "," "," "," "));
   g.put("turn","X");g.put("status","waiting");g.put("round",1L);g.put("hostAgain",false);g.put("guestAgain",false);g.put("hostWins",0L);g.put("guestWins",0L);g.put("createdAt",FieldValue.serverTimestamp());
   t.set(ref,g);return ref;
  });
 }
 Task<Void> join(String entered,String player){
  final String code=RoomCode.normalize(entered),guestName=RoomCode.player(player);
  if(!RoomCode.valid(code))return com.google.android.gms.tasks.Tasks.forException(new IllegalArgumentException("El código tiene 8 letras o números. Ejemplo: ABCD-2345."));
  final DocumentReference ref=games().document(code);
  return db.runTransaction(t->{
   DocumentSnapshot g=t.get(ref);
   if(!g.exists())throw new IllegalStateException("No existe una partida con ese código.");
   if(uid().equals(g.getString("host"))||uid().equals(g.getString("guest")))return null;
   if(!"waiting".equals(g.getString("status"))||!"".equals(g.getString("guest")))
    throw new IllegalStateException("La partida está completa o cerrada.");
   t.update(ref,"guest",uid(),"guestName",guestName,"status","playing");return null;
  });
 }
 Task<Void> change(String id,int cell){
  DocumentReference ref=games().document(id);
  return db.runTransaction(t->{
   DocumentSnapshot g=t.get(ref);
   if(!g.exists())throw new IllegalStateException("La partida no existe.");
   String phase=g.getString("status");
   String mark=uid().equals(g.getString("host"))?"X":uid().equals(g.getString("guest"))?"O":"";
   if(cell==-2){
    if(mark.isEmpty())throw new IllegalStateException("No perteneces a esta partida.");
    if(!"closed".equals(phase))t.update(ref,"status","closed");
   }else{
    if(mark.isEmpty()||!"playing".equals(phase)||!mark.equals(g.getString("turn")))throw new IllegalStateException("Espera tu turno.");
    TicTacToeGame b=new TicTacToeGame();b.restore(android.text.TextUtils.join("",(List<String>)g.get("board")));
    if(!b.setMove(mark.charAt(0),cell))throw new IllegalStateException("Casilla no disponible.");
    int result=b.result();t.update(ref,"board",Arrays.asList(b.save().split("")),"turn",mark.equals("X")?"O":"X","status",result==0?"playing":result==1?"X":result==2?"O":"draw",
     "hostWins",wins(g,"hostWins")+(result==1?1:0),"guestWins",wins(g,"guestWins")+(result==2?1:0));
   }return null;
  });
 }
 Task<Void> again(String id,long expectedRound){
  DocumentReference ref=games().document(id);
  return db.runTransaction(t->{
   DocumentSnapshot g=t.get(ref);
   boolean host=uid().equals(g.getString("host")),guest=uid().equals(g.getString("guest"));
   if(!g.exists()||(!host&&!guest))throw new IllegalStateException("No perteneces a esta partida.");
   long round=round(g);
   if(round!=expectedRound)throw new IllegalStateException("Ya comenzó otra ronda.");
   if(!Arrays.asList("X","O","draw").contains(g.getString("status")))throw new IllegalStateException("La ronda no ha terminado o la partida está cerrada.");
   boolean hostAgain=Boolean.TRUE.equals(g.getBoolean("hostAgain")),guestAgain=Boolean.TRUE.equals(g.getBoolean("guestAgain"));
   if((host&&hostAgain)||(guest&&guestAgain))return null;
   hostAgain=hostAgain||host;guestAgain=guestAgain||guest;
   if(hostAgain&&guestAgain){
    t.update(ref,"board",Arrays.asList(" "," "," "," "," "," "," "," "," "),"turn","X","status","playing","round",round+1,"hostAgain",false,"guestAgain",false);
   }else t.update(ref,"round",round,"hostAgain",hostAgain,"guestAgain",guestAgain);
   return null;
  });
 }
 static long round(DocumentSnapshot g){Long round=g.getLong("round");return round==null?1:round;}
 static long wins(DocumentSnapshot g,String key){Long count=g.getLong(key);return count==null?0:count;}
}
