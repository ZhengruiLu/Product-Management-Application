const express = require('express');
const mysql = require('mysql');

const app = express();
const PORT = 3000;

app.use(express.json()); // Parses requests with json content bodies

const con = mysql.createConnection({
    host:'localhost',
    user:'root',
    password: 'ChangChang@1',
    database:'userdb'
})

con.connect((err) =>{
    if(err){
        console.log(err)
    } else {
        console.log("Connected successfully!")
    }
})

function ValidateEmail(input) {
    var validRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
  
    if (input.match(validRegex)) {
      return true;
    } 
    
    return false;
}

// get user account information
app.get('/v1/user/:userId', (req, res) => {
    const userId = req.params.userId;

    if(!userId) {
        res.status(401).json({ error: 'Unauthorized' });
        return;
    }

    con.query(
        'select * from user where id=?',
        [userId],
        (err,result, fields)=>{
            if(err){
                console.log(err)
            } else {
                const r = JSON.parse(JSON.stringify(result))
                // console.log(r[0])
                // console.log(r[0].first_name)
                res.json(result)
            }
        }
    )
});


app.post('/v1/user', (req, res) => {

    const first_name = req.body.first_name;
    const last_name = req.body.last_name;
    const password = req.body.password;
    const username = req.body.username;

    if(!ValidateEmail(username)) {
        res.status(400).json({ error: 'Bad-Request' });
        return;
    }

    con.query(
        'insert into user(first_name, last_name, password, username) values(?,?,?,?)',
        [first_name, last_name, password, username],
        (err,result)=>{
            if(err){
                console.log("err: " + err)
                if (err == "Error: ER_DUP_ENTRY: Duplicate entry '"+ username +"' for key 'user.username'") {
                    res.status(400).json({ error: 'Bad-Request' });
                    return;
                }
            } else {
                res.json("User created")
            }
        }
    )
});

// update user account information
app.put('/v1/user/:userId', (req, res) => {
    const userId = req.params.userId;
    const first_name = req.body.first_name;
    const last_name = req.body.last_name;
    const password = req.body.password;
    const username = req.body.username;

    con.query(
        'update user set first_name=?, last_name=?, password=?, username=? where id=?',
        [first_name, last_name, password, username, userId],
        (err,result)=>{
            if(err){
                console.log(err)
            } else {

                if (result.affectedRows == 0) {
                    res.send("id not present")
                } else {
                    res.json("User info updated")
                    console.log(result)
                }

                res.send("POSTED")

            }
        }
    )
});

//health check
const http = require('http');

const router = express.Router();

router.use((req, res, next) => {
  res.header('Access-Control-Allow-Methods', 'GET');
  next();
});

router.get('/health', (req, res) => {
  res.status(200).send('Ok');
});

app.use('/healthz', router);


app.listen(PORT, (err) => {
    if(err){
        console.log(err)
    } else {
        console.log(`http://localhost:${PORT}`)
    }
});


