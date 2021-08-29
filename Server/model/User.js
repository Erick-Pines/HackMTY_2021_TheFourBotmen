const { Schema, model } = require("mongoose");

var postSchema = new Schema({
  user_id: Number,
  name: String,
  creation_date: { type: Date, default: Date.now },
  is_active: { type: Boolean, default: false },
  is_infected: { type: Boolean, default: false },
});

module.exports = model("User", postSchema);
