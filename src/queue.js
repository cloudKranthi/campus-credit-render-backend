const{Queue}=require('bullmq');
const connection={
    host:'127.0.0.1',
    port:6379
};
const dripQueue= new Queue('drip-queue',{connection});
module.exports={connection,dripQueue};