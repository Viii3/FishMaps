const WIDTH = 1920;
const HEIGHT = 1080;
const STEP = 40;

var currentX = 0;
var currentZ = 0;
var dimension = "minecraft:overworld";

function renderMap () {
    let map = document.getElementById("mapView");
    map.innerHTML = "";

    const topLeftX = Math.floor(currentX - (WIDTH / 2));
    const topLeftZ = Math.floor(currentZ - (HEIGHT / 2));
    for (let z = 0; z < HEIGHT / STEP; ++z) {
        for (let x = 0; x < WIDTH / STEP; ++x) {
            let image = document.createElement("img");
            image.class = "mapTile";
            image.style = "width:" + STEP + "px;height:" + STEP + "px;"
            image.src = "images/map?x=" + (topLeftX + currentX + x * STEP) + "&z=" + (topLeftZ + currentZ + z * STEP) + "&dimension=" + dimension + "&width=" + STEP + "&height=" + STEP;
            map.appendChild(image);
        }
        map.appendChild(document.createElement("br"));
    }
}

function up () {
    currentZ += STEP;
    renderMap();
}

function down () {
    currentZ -= STEP;
    renderMap();
}

function left () {
    currentX += STEP;
    renderMap();
}

function right () {
    currentX -= STEP;
    renderMap();
}