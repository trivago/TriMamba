const request = require("request");

const elasticURI = process.env.ELASTIC_URI;

if (elasticURI == undefined) {
    throw new Error("env variable ELASTIC_URI is undefined");
}

exports.elasticData = function () {
    this.name = "";
    this.datetime = {
        start: null,
        end: null
    };
    this.location = {
        adress: {
            street: "",
            number: ""
        },
        city: "",
        country: "",
        district: "",
        state: "",
        geo: {
            lat: null,
            lon: null
        }
    };
}

exports.write = function (data) {

    // TODO: Check if data is already present in DB

    return new Promise((resolve, reject) => {

        request.post(encodeURI(elasticURI + "/event-brite/_doc"), {
            headers: {
                "Content-Type": "application/json"
            },
            body: data,
            json: true
        }, (err, res, body) => {
            if (err != null || res.statusCode.toString().startsWith("2") == false) {
                console.error(JSON.stringify(res, null, 2));
                return reject(res);
            }

            console.log("Sent events to db", res.statusCode, res.statusMessage, data["name"]);
            return resolve();
        });

    });
}