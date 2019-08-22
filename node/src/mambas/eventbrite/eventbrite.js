const fs = require("fs");
const request = require("request");

const token = fs.readFileSync("./src/mambas/eventbrite/secret");
const uri = "https://www.eventbriteapi.com/v3/"

module.exports.Run = async () => {
    let response = await SearchEventsByAdress("Berlin");
    console.log(response);
}

/** Lists public Events from Eventbrite.
 * @param {string} adress The address of the location you want to search for Events around.
 * @param {string} radius The distance you want to search around the given location. This should be an integer followed by “mi” or “km”.
 * @returns Returns a paginated response.
 */
function SearchEventsByAdress(adress, radius) {
    return new Promise((resolve, reject) => {
        let uri =
            "https://www.eventbriteapi.com/v3/events/search?" +
            "expand=venue" + // Add extensive venue data
            "&location.address=" + adress;

        let radiusRegExp = new RegExp("\\d+(mi|km)");
        if (radius != null && radiusRegExp.test(radius)) {
            uri += "&location.within=" + radius;
        }

        request(encodeURI(uri), {
            'auth': {
                bearer: token
            }
        }, (err, res, body) => {
            if (err != null) {
                reject(error);
            }

            if (res.statusCode != 200) {
                console.error(res);
                reject(error);
            }

            resolve(body);
        });
    })
}