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

function isValidUsername(username) {
    let isValid = true;
    isValid = isValid && username.trim();
    isValid = isValid && username.match(/^[A-Za-z]+$/);
    return isValid;
}

app.post('/v1/user', (req, res) => {
    const first_name = req.body.first_name;
    const last_name = req.body.last_name;
    const password = req.body.password;
    const username = req.body.username;

    if(!isValidUsername(first_name) && isValidUsername(last_name)) {
        res.status(400).json({ error: 'Bad-Request' });
        return;
    }

    con.query(
        'insert into user(first_name, last_name, password, username) values(?,?,?,?)',
        [first_name, last_name, password, username],
        (err,result)=>{
            if(err){
                console.log(err)
            } else {
                res.json("User created")
            }
        }
    )
//   const { username } = req.body;

//   if(!users.isValidUsername(username)) {
//     res.status(400).json({ error: 'required-username' });
//     return;
//   }
//   if(username.toLowerCase() === 'dog') {
//     res.status(403).json({ error: 'auth-insufficient' });
//     return;
//   }
//   const sid = sessions.addSession(username);

//   const existingUserData = users.getGameState(username);

//   //check if user have game state or not, if not, create a new one
//   if (!existingUserData) {
//     users.addGame(username);
//   }

//   //To help with grading, the server will console.log() the username and the chosen secret word whenever a new game is started for a player
//   console.log("Home page - user name:" + username + ", secret word: " + users.nameAndGame[username].word);

//   res.cookie('sid', sid);
//   res.json(users.getGameState(username));
});

app.listen(PORT, (err) => {
    if(err){
        console.log(err)
    } else {
        console.log(`http://localhost:${PORT}`)
    }
});

// // Sessions
// // Check for existing session (used on page load)
// app.get('/api/session', (req, res) => {
//   const sid = req.cookies.sid;
//   const username = sid ? sessions.getSessionUser(sid) : '';
//   if(!sid || !username) {
//     res.status(401).json({ error: 'auth-missing' });
//     return;
//   }
//   res.json({ username });
// });

// // Create a new session (login)
// app.post('/api/session', (req, res) => {
//   const { username } = req.body;

//   if(!users.isValidUsername(username)) {
//     res.status(400).json({ error: 'required-username' });
//     return;
//   }
//   if(username.toLowerCase() === 'dog') {
//     res.status(403).json({ error: 'auth-insufficient' });
//     return;
//   }
//   const sid = sessions.addSession(username);

//   const existingUserData = users.getGameState(username);

//   //check if user have game state or not, if not, create a new one
//   if (!existingUserData) {
//     users.addGame(username);
//   }

//   //To help with grading, the server will console.log() the username and the chosen secret word whenever a new game is started for a player
//   console.log("Home page - user name:" + username + ", secret word: " + users.nameAndGame[username].word);

//   res.cookie('sid', sid);
//   res.json(users.getGameState(username));
// });

// //logout
// app.delete('/api/session', (req, res) => {
//   const sid = req.cookies.sid;
//   const username = sid ? sessions.getSessionUser(sid) : '';
//   if(sid) {
//     res.clearCookie('sid');
//   }
//   if(username) {
//     // Delete the session, but not the user data
//     sessions.deleteSession(sid);
//   }

//   res.json({ wasLoggedIn: !!username }); 
// });

// // get wordle user game state
// app.get('/api/word', (req, res) => {
//   const sid = req.cookies.sid;
//   const username = sid ? sessions.getSessionUser(sid) : '';
//   if(!sid || !username) {
//     res.status(401).json({ error: 'auth-missing' });
//     return;
//   }
//   res.json(users.getGameState(username));

// });

// //for user post guess word
// app.post('/api/word', (req, res) => {
//   const sid = req.cookies.sid;
//   const username = sid ? sessions.getSessionUser(sid) : '';
//   if(!sid || !username) {
//     res.status(401).json({ error: 'auth-missing' });
//     return;
//   }
//   const { word } = req.body;
//   if(!word && word !== '') {
//     res.status(400).json({ error: 'required-word' });
//     return;
//   }
//   if(!users.isValidWord(word)) {
//     res.status(400).json({ error: 'invalid-word' });
//     return;
//   }
//   //update word and gameState
//   users.updateGuessWord(username, word);

//   res.json(users.getGameState(username));
// });

// //for user post guess word
// app.post('/api/new', (req, res) => {
//   const sid = req.cookies.sid;
//   const username = sid ? sessions.getSessionUser(sid) : '';

//   if(!sid || !username) {
//     res.clearCookie('sid');
//     res.status(401).json({ error: 'auth-missing' });
//     return;
//   }

//   users.addGame(username);

//   console.log("Start new game - user name:" + username + ", secret word: " + users.nameAndGame[username].word);
  
//   res.json(users.getGameState(username));
// });


