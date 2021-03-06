const express = require("express");
const path = require("path");
const morgan = require("morgan");
const mongoose = require("mongoose");

var Location = require("./model/Location");
var User = require("./model/User");

// server using express
const app = express();
const port = 3000;

var active_users = [];
var latest_locations = [];
var min_distance = 0.0;

mongoose
  .connect("mongodb://localhost/hack_data", {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  })
  .then((db) => console.log("db connected"))
  .catch((err) => console.log(err));

var db = mongoose.connection;

db.once("open", () => {
  //console.log(`Database connected to: ${"mongodb://localhost/hack_data"}`);
  scanActiveUsers();
});

db.on("error", (error) => {
  //console.log(error);
});

// middlewares
app.use(morgan("dev"));
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

app.listen(port, () => {
  //console.log(`Example app listening at http://localhost:${port}`);
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

app.post("/location/new", (req, res) => {
  var user_id = req.body.user_id;
  active_users = [];
  scanActiveUsers();

  console.log(`${req.body.user_id}\t${req.body.lat}\t${req.body.lon}`);
  /*
  var location = new Location({
    user_id: user_id,
    lat: req.body.lat,
    lon: req.body.lon,
  });

  location.save((err, document) => {
    if (err) {
      //console.log(err);
      return;
    }
  });

  getUserLatestLocation(user_id);

  setTimeout(() => {
    // //console.log("Hello");
    // //console.log(latest_locations);

    var collisions = getCollisionList(uniqueIds(latest_locations), user_id);
    // //console.log(`Collisions: ${collisions}`);

    for (entry of collisions) {
      //console.log(`${entry.from} - ${entry.with}`);
    }

    if (collisions.length > 0) {
      console.log("true");
      res.json({
        user_id: user_id,
        is_colliding: true,
        // collisions: collisions,
      });
    } else {
      console.log("false");
      res.json({
        user_id: user_id,
        is_colliding: false,
      });
    }
  }, 1000);

  console.log(latest_locations);

  //   let getLocations = new Promise((resolve, reject) => {
  //     getUsersLatestLocations(active_users);
  //     resolve(true);
  //   }).then(() => {
  //     //console.log(latest_locations);
  //   });

  console.log(`Locations: ${latest_locations}`);*/
});

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

app.post("/user/infected", (req, res) => {
  //console.log("User infected");

  let user_id = req.body.user_id;

  User.findOne({ user_id: user_id }, (err, user) => {
    if (err) {
      //console.log(err);
      return;
    }

    if (user == null) {
      //console.log("User does not exist");
      return;
    }

    if (!user.is_infected) {
      user.is_infected = true;
      //console.log(`User ${user.user_id} is infected!`);
    } else {
      user.is_infected = false;
      //console.log(`User ${user.user_id} is no longer infected!`);
    }

    user.save();
  });

  res.end();
});

// Toggle user active/inactive state
app.post("/user/active", (req, res) => {
  //console.log("User active/inactive");
  //console.log(req.body);
  let user_id = req.body.user_id;

  User.findOne({ user_id: user_id }, (err, user) => {
    if (err) {
      //console.log(err);
      return;
    }

    if (user == null) {
      //console.log("User does not exist");
      return;
    }

    if (!user.is_active) {
      active_users.push(user_id);
      user.is_active = true;
    } else {
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
      //console.log(users);
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
