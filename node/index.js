const scheduler = require("./src/Scheduler");
const eventbrite = require("./src/mambas/eventbrite/eventbrite");

scheduler.Add(eventbrite);