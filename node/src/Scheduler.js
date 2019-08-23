const scheduler = require("node-schedule");

const rule = new scheduler.RecurrenceRule();
rule.second = 0;

module.exports.Add = (crawler) => {
    try {
        crawler.Run();
    } catch (error) {
        throw error;
    }

    if (process.env.DEBUG = true) {
        console.warn("Debug is enabled. Scheduler disabled and only running crawlers once.");
        return;
    }

    scheduler.scheduleJob(rule, () => {
        console.log("Running job:", crawler);
        crawler.Run();
    });
}