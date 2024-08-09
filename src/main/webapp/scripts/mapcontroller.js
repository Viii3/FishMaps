var width = 1920;
var height = 1080;
const STEP = 40;

var currentX = 0;
var currentZ = 0;
var dimension = "minecraft:overworld";
var scale = 1;

function renderMap () {
    width = window.outerWidth;
    height = window.outerHeight;

    let map = document.getElementById("mapView");
    map.innerHTML = "";

    const scaledStep = Math.floor(STEP * scale);
    const topLeftX = Math.floor(currentX - (width / 2));
    const topLeftZ = Math.floor(currentZ - (height / 2));
    for (let z = 0; z < height / scaledStep; z += scaledStep) {
        for (let x = 0; x < width / scaledStep; x += scaledStep) {
            let image = document.createElement("img");
            image.class = "mapTile";
            image.style = "width:" + STEP + "px;height:" + STEP + "px;"
            image.src = "images/map?x=" + (topLeftX + currentX + x) + "&z=" + (topLeftZ + currentZ + z) + "&dimension=" + dimension + "&width=" + STEP + "&height=" + STEP + "&scale=" + scale;
            map.appendChild(image);
        }
        map.appendChild(document.createElement("br"));
    }
}

function up () {
    currentZ += STEP;
    updateURL();
    renderMap();
}

function down () {
    currentZ -= STEP;
    updateURL();
    renderMap();
}

function left () {
    currentX += STEP;
    updateURL();
    renderMap();
}

function right () {
    currentX -= STEP;
    updateURL();
    renderMap();
}

function parseURL () {
    const query = new URLSearchParams(window.location.search);

    if (query.has("x")) currentX = parseIntParam(query.get("x"));
    if (query.has("z")) currentZ = parseIntParam(query.get("z"));
    if (query.has("dimension")) dimension = query.get("dimension");
}

function updateURL () {
    const query = new URLSearchParams(window.location.search);
    query.set("x", currentX);
    query.set("z", currentZ);
    query.set("dimension", dimension);
    
    window.history.replaceState(null, null, "map.xhtml?" + query.toString());
}

function parseIntParam (param) {
    let num = Number(param);
    if (num == NaN) return 0;
    return Math.floor(num);
}

function startup () {
    parseURL();
    updateURL();
    renderMap();
}