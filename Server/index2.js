const express = require("express");
const path = require("path");
const morgan = require("morgan");

var Location = require("./model/Location");
var User = require("./model/User");

// server using express
const app = express();
const port = 3000;

var active_users = [];
var latest_locations = [];
var min_distance = 0.0;

var user_data = [];

// middlewares
app.use(morgan("dev"));
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`);
});

///////////////////////////// GET ROUTES /////////////////////////////

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

app.get("/location/latest", (req, res) => {
  res.end();
});

///////////////////////////// POST ROUTES /////////////////////////////
// Receive user current location
app.post("/location/new", (req, res) => {
  id = req.body.user_id;
  user_data[id] = {
    lat: req.body.lat * 111000,
    lon: req.body.lon * 111000,
  };
  var collision = false;
  var collision_infected = false;

  console.log(user_data[id]);

  for (var i = 0; i < user_data.length; i++) {
    if (id != i)
      if (user_data[i] != undefined) {
        if (
          distance(
            user_data[id].lat,
            user_data[id].lon,
            user_data[i].lat,
            user_data[i].lon
          ) <= 0.00006214
        )
          collision = true;

        if (user_data[i].is_infected) collision_infected = true;

        console.log(
          distance(
            user_data[id].lat,
            user_data[id].lon,
            user_data[i].lat,
            user_data[i].lon
          )
        );
      }
  }
  res.json({
    user_id: id,
    is_colliding: collision,
    collision_infected: collision_infected,
  });
});

// Create new user
app.post("/user/new", (req, res) => {
  var user = new User({
    user_id: req.body.user_id,
    name: req.body.name,
  });

  user.save((err, document) => {
    if (err) {
      //console.log(err);
      return;
    }
  });

  res.end();
});

// Update when a user is infected from COVID
app.post("/user/infected", (req, res) => {
  let user_id = req.body.user_id;
  let is_infected = req.body.is_infected;

  User.findOne({ user_id: user_id }, (err, user) => {
    if (err) {
      console.log(err);
      return;
    }

    if (user == null) {
      console.log("User does not exist");
      return;
    }

    user.is_infected = is_infected;

    user.save();
  });

  res.end();
});

// Toggle user active/inactive state
app.post("/user/active", (req, res) => {
  let user_id = req.body.user_id;
  let is_active = req.body.is_active;

  User.findOne({ user_id: user_id }, (err, user) => {
    if (err) {
      console.log(err);
      return;
    }

    if (user == null) {
      console.log("User does not exist");
      return;
    }

    if (is_active) {
      if (!active_users.includes(user_id)) active_users.push(user_id);
      user.is_active = true;
    } else {
      if (active_users.includes(user_id))
        active_users.splice(active_users.indexOf(user_id), 1);
      user.is_active = false;
    }

    user.save();

    //console.log(`Active users: ${active_users}`);
  });

  res.end();
});

///////////////////////////// HELPER FUNCTIONS /////////////////////////////

// Init function to scan all active users
function scanActiveUsers() {
  //console.log("Init: scan active users");
  User.find({ is_active: true }, (err, users) => {
    if (err) console.log("Init: could not retrieve active users");
    else {
      for (var user of users) {
        active_users.push(user.user_id);
      }
      console.log(users);
    }

    // //console.log(`Active users: ${active_users}`);
  });
}

// Get latest locations given a user id
async function getUserLatestLocation() {
  //   //console.log(`Active pre for: ${active_users}`);
  for (let user_id of active_users) {
    Location.find({ user_id: user_id })
      .sort({ date: -1 })
      .limit(1)
      .then((location) => {
        latest_locations.push(location[0]);
      });
  }
}

function distance(lat, lon, lat2, lon2) {
  return Math.sqrt(Math.pow(lon2 - lon, 2) + Math.pow(lat2 - lat, 2));
}

function getCollisionList(data, id) {
  if (data.length == 0) return [];

  let collisions = [];
  let current_index = 0;
  for (let i = 0; i < data.length; i++) {
    if (data[i] == undefined) continue;
    if (data[i].user_id == id) current_index = i;
  }
  let current = data[current_index];
  for (let j = 0; j < data.length; j++) {
    if (data[j] == undefined) continue;
    let other = data[j];
    if (current_index != j) {
      // if distance is shorter than aproximately 2.5 m
      min_distance = Math.max(
        min_distance,
        distance(current.lat, current.lon, other.lat, other.lon)
      );
      console.log(distance(current.lat, current.lon, other.lat, other.lon));
      if (min_distance <= 0.0002)
        collisions.push({
          from: current.user_id,
          with: other.user_id,
        });
    }
  }

  //console.log(min_distance);
  return collisions;
}

function uniqueIds(data) {
  var new_data = data;
  for (let i = 0; i < new_data.length; i++) {
    if (new_data[i] == undefined) {
      new_data.splice(i, 1);
      i--;
    }
  }
  //console.log(new_data);
  for (let i = 0; i < new_data.length; i++) {
    for (let j = i + 1; j < new_data.length; j++) {
      if (new_data[i].user_id == new_data[j].user_id) {
        //console.log(new_data[i]);
        //console.log(j);
        new_data.splice(j, 1);
        j--;
      }
    }
  }
  //console.log(data);
  return new_data;
}
