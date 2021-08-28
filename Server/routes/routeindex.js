const express = require("express");
const router = express.Router();

router.get("/", (req, res) => {
  res.send("The four Botmen");
});

router.get("/test", (req, res) => {
  res.send("Test");
});

module.exports = router;
