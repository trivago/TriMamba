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
        location: {
            lat: null,
            lon: null
        }
    };
}

exports.write = function (data) {

}