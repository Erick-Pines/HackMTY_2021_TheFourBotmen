const express = require("express");
const path = require("path");
const morgan = require("morgan");
const mongoose = require("mongoose");

var Location = require("./model/Location");
var User = require("./model/User");

// server using express
const app = express();
const port = 3000;

let activeUsers = [];

mongoose
  .connect("mongodb://localhost/hack_data", {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  })
  .then((db) => console.log("db connected"))
  .catch((err) => console.log(err));

var db = mongoose.connection;

db.once("open", () => {
  console.log(`Database connected to: ${"mongodb://localhost/hack_data"}`);
  scanActiveUsers();
});

db.on("error", (error) => {
  console.log(error);
});

// middlewares
app.use(morgan("dev"));
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`);
});

app.get("/", (req, res) => {
  res.send("The four Botmen");
});

app.get("/test", (req, res) => {
  res.send("Test");
});

app.get("/json", (req, res) => {
  res.json({
    user_id: 1,
    name: "Python",
    creation_date: Date.now,
    is_active: true,
  });
});

app.post("/location/new", (req, res) => {
  var location = new Location({
    user_id: 1,
    lat: 0.0,
    lon: 0.0,
  });

  location.save((err, document) => {
    if (err) {
      console.log(err);
      return;
    }
  });

  res.end();
});

app.post("/user/new", (req, res) => {
  var user = new User({
    user_id: req.body.user_id,
    name: req.body.name,
  });

  user.save((err, document) => {
    if (err) {
      console.log(err);
      return;
    }
  });

  res.end();
});

app.post("/user/infected", (req, res) => {
  console.log("User infected");
  res.end();
});

// Toggle user active/inactive state
app.post("/user/active", (req, res) => {
  console.log("User active/inactive");
  let user_id = req.body.user_id;

  User.findOne({ user_id: user_id }, (err, user) => {
    if (err) {
      console.log(err);
      return;
    }

    if (user == null) {
      console.log("User does not exist");
      return;
    }

    if (!user.is_active) {
      activeUsers.push(user_id);
      user.is_active = true;
    } else {
      activeUsers.splice(activeUsers.indexOf(user_id), 1);
      user.is_active = false;
    }

    user.save();

    console.log(activeUsers);
  });

  res.end();
});

// Init function to scan all active users
function scanActiveUsers() {
  console.log("Init: scan active users");
  User.find({ is_active: true }, (err, users) => {
    if (err) console.log("Init: could not retrieve active users");
    else
      for (var user of users) {
        activeUsers.push(user.user_id);
      }

    console.log(activeUsers);
  });
}
