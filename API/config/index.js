var app    = require('express')();


app.use(require('body-parser').json());

module.exports = app;