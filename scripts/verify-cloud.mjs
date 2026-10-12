import fs from 'node:fs';
import crypto from 'node:crypto';
import assert from 'node:assert/strict';
const root=new URL('../',import.meta.url);
const config=JSON.parse(fs.readFileSync(new URL('app/google-services.json',root),'utf8'));
const project=config.project_info.project_id;
assert.equal(project,'reto7jugandotriquionline');
const client=config.client.find(c=>c.client_info.android_client_info.package_name==='com.hernandovela.reto7');
assert.ok(client,'Falta el paquete Android correcto');
const key=client.api_key[0].current_key;
const base='https://firestore.googleapis.com/v1/projects/'+project+'/databases/(default)/documents';
async function account(){
 const r=await fetch('https://identitytoolkit.googleapis.com/v1/accounts:signUp?key='+encodeURIComponent(key),{
  method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({returnSecureToken:true})});
 const data=await r.json();assert.equal(r.status,200,'Authentication: '+(data.error?.message||r.status));
 return {uid:data.localId,token:data.idToken};
}
function value(v){
 if(Array.isArray(v))return {arrayValue:{values:v.map(value)}};
 if(typeof v==='boolean')return {booleanValue:v};
 if(typeof v==='number')return {integerValue:String(v)};
 return {stringValue:v};
}
function fields(data){return Object.fromEntries(Object.entries(data).map(([k,v])=>[k,value(v)]));}
const host=await account(),guest=await account(),third=await account();
const alphabet='ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
const id=Array.from(crypto.randomBytes(8),b=>alphabet[b%alphabet.length]).join('');
const path='projects/'+project+'/databases/(default)/documents/games_v2/'+id;
async function commit(who,data,mask,precondition,created=false,expected=200){
 if(mask?.includes('board') && !mask.includes('round') && expected===200){data={...data,hostWins:data.status==='X'?1:0,guestWins:data.status==='O'?1:0};mask=[...mask,'hostWins','guestWins'];}
 const write={update:{name:path,fields:fields(data)}};
 if(mask)write.updateMask={fieldPaths:mask};
 if(precondition)write.currentDocument=precondition;
 if(created)write.updateTransforms=[{fieldPath:'createdAt',setToServerValue:'REQUEST_TIME'}];
 const r=await fetch(base+':commit',{method:'POST',headers:{Authorization:'Bearer '+who.token,'Content-Type':'application/json'},body:JSON.stringify({writes:[write]})});
 const body=await r.json();assert.equal(r.status,expected,'Firestore: '+(body.error?.message||r.status));
 return body;
}
async function read(who){
 const r=await fetch(base+'/games_v2/'+id,{headers:{Authorization:'Bearer '+who.token}});
 assert.equal(r.status,200);return r.json();
}
let created=false;
try{
 await commit(host,{host:host.uid,guest:'',name:'Verificación nube · Reto 7',code:id,hostName:'Ana',guestName:'',board:Array(9).fill(' '),turn:'X',status:'waiting'},null,{exists:false},true);created=true;
 let doc=await read(host);
 // Competing joins use the same server version: exactly one must succeed.
 const joins=await Promise.all([guest,third].map(async who=>{
  const write={update:{name:path,fields:fields({guest:who.uid,guestName:'Luis',status:'playing'})},updateMask:{fieldPaths:['guest','guestName','status']},currentDocument:{updateTime:doc.updateTime}};
  const r=await fetch(base+':commit',{method:'POST',headers:{Authorization:'Bearer '+who.token,'Content-Type':'application/json'},body:JSON.stringify({writes:[write]})});return {who,status:r.status};
 }));
 assert.equal(joins.filter(j=>j.status===200).length,1,'Solo un jugador debe ocupar el segundo cupo');
 const opponent=joins.find(j=>j.status===200).who,outsider=joins.find(j=>j.status!==200).who;
 await commit(outsider,{guest:outsider.uid,status:'playing'},['guest','status'],null,false,403);
 let board=Array(9).fill(' ');
 // O cannot start; outsiders cannot move; occupied squares and changed host are forbidden.
 board[0]='O';await commit(opponent,{board,turn:'X',status:'playing'},['board','turn','status'],null,false,403);
 board[0]='X';await commit(outsider,{board,turn:'O',status:'playing'},['board','turn','status'],null,false,403);
 await commit(host,{board,turn:'O',status:'playing'},['board','turn','status']);
 await commit(host,{host:outsider.uid},['host'],null,false,403);
 board[0]='O';await commit(opponent,{board,turn:'X',status:'playing'},['board','turn','status'],null,false,403);
 board[0]='X';
 for(const [who,cell,mark,next,status] of [[opponent,3,'O','X','playing'],[host,1,'X','O','playing'],[opponent,4,'O','X','playing'],[host,2,'X','O','X']]){
  board[cell]=mark;await commit(who,{board,turn:next,status},['board','turn','status']);
 }
 doc=await read(opponent);assert.equal(doc.fields.hostWins.integerValue,'1');assert.equal(doc.fields.guestWins.integerValue,'0');assert.equal(doc.fields.status.stringValue,'X'); assert.equal(doc.fields.hostName.stringValue,'Ana'); assert.equal(doc.fields.guestName.stringValue,'Luis'); assert.equal(doc.fields.code.stringValue,id);
 await commit(opponent,{hostName:'Cambio no autorizado'},['hostName'],null,false,403);
 await commit(host,{hostWins:99},['hostWins'],null,false,403);
 await commit(host,{code:'ABCD2345'},['code'],null,false,403);
 assert.deepEqual(doc.fields.board.arrayValue.values.map(v=>v.stringValue),board);
 board[8]='O';await commit(opponent,{board,turn:'X',status:'X'},['board','turn','status'],null,false,403);
 await commit(outsider,{round:1,hostAgain:true,guestAgain:false},['round','hostAgain','guestAgain'],null,false,403);
 await commit(host,{board:Array(9).fill(' '),turn:'X',status:'playing',round:2,hostAgain:false,guestAgain:false},['board','turn','status','round','hostAgain','guestAgain'],null,false,403);
 await commit(host,{round:1,hostAgain:true,guestAgain:false},['round','hostAgain','guestAgain']);
 doc=await read(opponent);assert.equal(doc.fields.status.stringValue,'X');assert.equal(doc.fields.hostAgain.booleanValue,true);
 await commit(host,{round:1,hostAgain:true,guestAgain:true},['round','hostAgain','guestAgain'],null,false,403);
 await commit(opponent,{board:Array(9).fill(' '),turn:'X',status:'playing',round:2,hostAgain:false,guestAgain:false},['board','turn','status','round','hostAgain','guestAgain']);
 doc=await read(host);assert.equal(doc.fields.hostWins.integerValue,'1');assert.equal(doc.fields.guestWins.integerValue,'0');assert.equal(doc.fields.round.integerValue,'2');assert.equal(doc.fields.code.stringValue,id);assert.equal(doc.fields.hostName.stringValue,'Ana');
 assert.deepEqual(doc.fields.board.arrayValue.values.map(v=>v.stringValue),Array(9).fill(' '));
 await commit(host,{round:2,hostAgain:true,guestAgain:false},['round','hostAgain','guestAgain'],null,false,403);
 await commit(host,{status:'closed'},['status']);
 await commit(opponent,{board:Array(9).fill(' '),turn:'X',status:'playing',round:3,hostAgain:false,guestAgain:false},['board','turn','status','round','hostAgain','guestAgain'],null,false,403);
 console.log('PASS 1.3.0: marcador sin duplicados ni manipulación, códigos, nombres, turnos, victoria, consentimiento de ambos para revancha, mismo código, ronda siguiente y cierre definitivo.');
}finally{
 if(created){
  const doc=await read(host);
  if(['waiting','playing'].includes(doc.fields.status.stringValue))
   await commit(host,{status:'closed'},['status']);
 }
}
