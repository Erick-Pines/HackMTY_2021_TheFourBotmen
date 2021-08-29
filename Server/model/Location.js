const { Schema, model } = require("mongoose");

var postSchema = new Schema({
  user_id: Number,
  lat: Number,
  lon: Number,
  date: { type: Date, default: Date.now },
});

module.exports = model("Location", postSchema);
