const fs = require("fs");
const request = require("request");

const token = fs.readFileSync("./mambas/eventbrite/secret");

var exports = module.exports = {}

exports.Init = function () {
    Test();
}

function Test() {
    let uri = encodeURI("https://www.eventbriteapi.com/v3/events/search?location.address=berlin&location.within=10km&expand=venue");

    request(uri, {
        'auth': {
            bearer: token
        }
    }, (err, res, body) => {
        if (err != null) {
            console.error(error);
            return;
        }

        if (res.statusCode != 200) {
            console.error(body);
            return;
        }

        console.log(body);
    });
}