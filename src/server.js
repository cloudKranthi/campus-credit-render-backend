
import express from 'express';
import { dripQueue } from './queue.js';
const app=express();
app.use(express.json());
app.use("/api/adddrip/valut",
 async  function handlerequest(req,res){
  const{parentphoneNumber,studentphoneNumber,dailyLimit,balanceAmount}=req.body;
  await dripQueue.add("drip-payouts",{
    parentphoneNumber,
    studentphoneNumber,
    dailyLimit,
    balanceAmount
  },{
    delay:10*1000
  });
  console.log('Drip initiated in redis');
  res.send("Money sended succesfully")
});
app.listen(3000,()=>{
  console.log('Node server lisetining on 3000')
});
