const {Pool}=require('pg');
const pool=new Pool({
    connectionString:'postgresql://postgres:Shiva@37@localhost:5432/wallet_db'
});
module.exports=pool;
