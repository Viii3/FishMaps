const WIDTH = 1080;
const HEIGHT = 720;

var currentX = 0;
var currentZ = 0;

function renderMap () {
    let map = document.getElementById("mapView");
    map.innerHTML = "";
    let image = document.createElement("img");
    image.src = "images/map?x=" + currentX + "&z=" + currentZ + "&dimension=minecraft:overworld&width=" + WIDTH + "&height=" + HEIGHT;
    map.appendChild(image);
}

function up () {
    currentZ++;
    renderMap();
}

function down () {
    currentZ--;
    renderMap();
}

function left () {
    currentX--;
    renderMap();
}

function right () {
    currentX++;
    renderMap();
}