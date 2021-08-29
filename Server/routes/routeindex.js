const express = require("express");
const router = express.Router();
const index = require("../index");

var Location = require("../model/Location");
var User = require("../model/User");

router.get("/", (req, res) => {
  res.send("The four Botmen");
});

router.get("/test", (req, res) => {
  res.send("Test");
});

router.get("/json", (req, res) => {
  res.json({
    user_id: 1,
    name: "Python",
    creation_date: Date.now,
    is_active: true,
  });
});

router.post("/location/new", (req, res) => {
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

router.post("/user/new", (req, res) => {
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

router.post("/user/infected", (req, res) => {
  console.log("User infected");
  res.end();
});

// Toggle user active/inactive state
router.post("/user/active", (req, res) => {
  console.log("User active/inactive");
  console.log(index.getActiveUsers);
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
  });

  res.end();
});

function updateActiveUsers(id) {}

module.exports = router;
