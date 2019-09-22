console.log("MyDocument is ", document.title);
var loaded = false;
var currentCity = parseCity();
window.onload = function () {
    addOnSearchListener();
}

async function showEvents(size) {
    var city = parseCity();
    var dates = parseDates();
    var events = await getData(city, dates, size);
    var html = generateHtml(city, events);
    var itemlist = document.getElementById("js_itemlist_controls");
    itemlist.innerHTML = itemlist.innerHTML + html;
    //  var loadMore = document.getElementById("load-more");
    //  loadMore.onclick = function () { showEvents(5); };
}

function addOnSearchListener() {
    var element = document.getElementById("js_item_list_section");
    console.log(element);
    var observer = new MutationObserver(function (mutations) {
        mutations.forEach(function (mutation) {
            if (mutation.type == "attributes") {
                var city = parseCity();
                if (city === currentCity) {
                    showEvents(10);
                } else {
                    showEvents(5);
                }
            }
        });
    });

    observer.observe(element, {
        attributes: true
    });
}

async function getData(city, dates, size) {
    var requestBody = `{ "query": { "bool": { "must": [ { "match": { "location.city": "${city}" } }, { "range": { "datetime.start": { "gte": "${dates[0]}", "lte": "${dates[1]}" } } } ] } }, "size" : ${5}, "sort": ["datetime.start"] }`;
    console.log(requestBody);
    var response = await fetch('https://elastic.fabian-fritzsche.de/event-bundesliga,event-brite/_search', {
        method: 'post',
        body: requestBody,
        headers: { 'Content-type': 'application/json' }
    });
    var data = await response.json();
    console.log(data);
    var events = data.hits.hits.map(item => convertToEvent(item._source));

    return events;
}

function parseCity() {
    var city = document.title.replace(" Hotels | Find & compare great deals on trivago", "");
    return city;
}

function parseDates() {
    var startDateText = document.getElementsByClassName("dealform-button__label")[0].innerText;
    var endDateText = document.getElementsByClassName("dealform-button__label")[1].innerText;
    var startDate = moment(startDateText, "dd, MM/DD/YY").add(-1, 'days').format("YYYY-MM-DD");
    var endDate = moment(endDateText, "dd, MM/DD/YY").add(1, 'days').format("YYYY-MM-DD");
    return [startDate, endDate];
}

function formatDate(start, end) {
    var date = moment(start).utc().format("DD.MM, dd HH:mm") + " - " + moment(end).utc().format("HH:mm");
    return date;
}

function convertToEvent(source) {
    let event = {};
    event.name = source.name;
    event.time = formatDate(source.datetime.start, source.datetime.end);
    if (source.location.address == null) {
        event.address = "";
    } else {
        event.address = source.location.address;
    }

    return event;
}

function generateHtml(city, events) {
    var titleStyle = "font-weight: bold; margin: 0px; margin-left: 10px; font-size: 16px; color: #00526f;"
    var pStyle = "margin: 2px; margin-left: 10px; font-size: 14px;"
    var aStyle = " color: #003e54; font-weight: bold;"
    var spanStyle = "display: flex; flex-direction: row-reverse; justify-content: left; align-items: left; width: 20px;";
    var divStyle = "box-shadow: 0 1px 4px rgba(41,51,57,.5); color: #37454d; margin-bottom: 8px; position: relative; width: 100%;"

    var items = "";
    events.forEach(event => {
        items += `
        <p style="${pStyle}">
            <a style="${aStyle}">
             <span style="color: #077fb8">${event.time} </span> , 
            ${event.name} @ ${event.address}
             </a> 
        </p>`;
    });
    var element = document.getElementById("upcoming-events");
    if (element !== null) {
        element.remove();
    }
    var html =
        `<div id="upcoming-events" class="bg-white" style="height: 140px; margin-top: 20px; ${divStyle}">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span style="${titleStyle}">Upcoming events in ${city}</span>
                    <a id="load-more" style="margin-right: 10px; font-size: 14px;">Load More</a>
                </div>
                    ${items}
        </div>`;

    return html;
}