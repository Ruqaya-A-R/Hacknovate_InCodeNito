const express = require('express'); 
const path = require('path');       
const open = require('open');
const app = express();               
const port = 3000;

// Middleware to parse form data
app.use(express.urlencoded({ extended: true }));

// Serve static files from public folder
app.use(express.static(path.join(__dirname, 'public')));

// Routes
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'login.html'));
});

app.post('/login', (req, res) => {
    const { username, password } = req.body;
    if (username === 'admin' && password === 'admin123') {
        res.sendFile(path.join(__dirname, 'public', 'login.html'));
    } else {
        res.send('Invalid username or password!');
    }
});
res.redirect('/main');

// Start server
app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
