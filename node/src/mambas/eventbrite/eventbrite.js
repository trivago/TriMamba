const fs = require("fs");
const request = require("request");
const elasticWriter = require("../../ElasticWriter");

const token = fs.readFileSync("./src/mambas/eventbrite/secret");
const uri = "https://www.eventbriteapi.com/v3/"

module.exports.Run = async () => {
    await searchEventsByAdress("Berlin").then((data) => {
        data["events"].forEach(event => {
            parseToElasticData(event);
        });
    });
}

/** Lists public Events from Eventbrite.
 * @param {string} adress The address of the location you want to search for Events around.
 * @param {string} radius The distance you want to search around the given location. This should be an integer followed by “mi” or “km”.
 * @returns Returns a paginated response.
 */
function searchEventsByAdress(adress, radius) {

    console.log("Searching by adress", adress, "with radius", radius);

    return new Promise((resolve, reject) => {
        let uriRequest =
            uri +
            "events/search?" +
            "expand=venue" + // Add extensive venue data
            "&location.address=" + adress;

        let radiusRegExp = new RegExp("\\d+(mi|km)");
        if (radius != null && radiusRegExp.test(radius)) {
            uriRequest += "&location.within=" + radius;
        }

        request(encodeURI(uriRequest), {
            'auth': {
                bearer: token
            }
        }, (err, res, body) => {
            if (err != null) {
                console.error(err);
                reject(err);
            }

            if (res.statusCode != 200) {
                console.error(res);
                reject(res);
            }

            console.log("Requested events", res.statusCode, res.statusMessage);

            let json = JSON.parse(body);

            resolve(json);
        });
    })
}

function getVenueDataFromEvent(event) {
    return event["venue"];
}

function parseToElasticData(event) {
    new Promise
    let elasticData = new elasticWriter.elasticData();

    elasticData.name = event["name"]["text"];
    elasticData.datetime = {
        start: event["start"]["utc"],
        end: event["end"]["utc"]
    }

    let venue = event["venue"];
    let adress = venue["address"];

    elasticData.location = {
        adress: adress["address_1"],
        city: adress["city"],
        country: adress["country"],
        state: adress["region"],
        location: {
            lat: adress["latitude"],
            lon: adress["longitude"]
        }
    }
}