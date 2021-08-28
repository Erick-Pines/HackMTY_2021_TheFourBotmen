const express = require("express");
const path = require("path");
const morgan = require("morgan");
const mongoose = require("mongoose");

// server using express
const app = express();
const port = 3000;

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
});

db.on("error", (error) => {
  console.log(error);
});

// importing routes
const indexRoutes = require("./routes/routeindex");

// middlewares
app.use(morgan("dev"));
app.use(express.urlencoded({ extended: false }));

// routes
app.use("/", indexRoutes);

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`);
});
