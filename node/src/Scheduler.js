const scheduler = require("node-schedule");

const rule = new scheduler.RecurrenceRule();
rule.second = 0; // Runs once per minute/everytime second === 0

/** Adds a crawler to the overall scheduler
 * @param {*} crawler Crawler to add to the scheduler
 */
module.exports.Add = (crawler) => {

    // try to run the debugger for the first time
    try {
        crawler.Run();
    } catch (error) {
        throw error;
    }

    // If debug flag is enabled, scheduler is disabled and only runs the crawler once
    if (process.env.DEBUG == true) {
        console.warn("Debug is enabled. Scheduler disabled and only running crawlers once.");
        return;
    }

    // Add job to the scheduler
    scheduler.scheduleJob(rule, () => {
        console.log("Running job:", crawler);
        crawler.Run();
    });
}