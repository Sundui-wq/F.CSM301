

let map;
let startMarker = null;
let endMarker = null;
let startCoords = null;
let endCoords = null;
let pathLayers = [];
let selectedAlgorithm = null;
let nodeMarkersLayer = null;

const UB_CENTER = [47.92, 106.92];
const UB_ZOOM = 12;

document.addEventListener('DOMContentLoaded', function() {
    initMap();
    initEventListeners();
    loadGraphStats();
});

function initMap() {
    map = L.map('map').setView(UB_CENTER, UB_ZOOM);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19
    }).addTo(map);

    nodeMarkersLayer = L.layerGroup().addTo(map);

    map.on('click', onMapClick);
}

function initEventListeners() {
    document.getElementById('btn-bfs').addEventListener('click', () => selectAlgorithm('bfs'));
    document.getElementById('btn-dfs').addEventListener('click', () => selectAlgorithm('dfs'));
    document.getElementById('btn-dijkstra').addEventListener('click', () => selectAlgorithm('dijkstra'));
    document.getElementById('btn-compare').addEventListener('click', compareAllAlgorithms);

    document.getElementById('btn-clear').addEventListener('click', clearAll);
    document.getElementById('btn-reset').addEventListener('click', resetPoints);

    document.getElementById('btn-show-nodes').addEventListener('click', toggleShowAllNodes);
}


let nodesVisible = false;
async function toggleShowAllNodes() {
    const btn = document.getElementById('btn-show-nodes');

    if (nodesVisible) {
        nodeMarkersLayer.clearLayers();
        nodesVisible = false;
        btn.textContent = '📍 Бүх цэгүүд';
        btn.classList.remove('active');
    } else {
        showLoading(true);

        try {
            const response = await API.getAllNodes();

            if (response && response.success && response.nodes) {
                const nodes = response.nodes;
                console.log(`Нийт ${nodes.length} цэг олдлоо`);

                nodes.forEach(node => {
                    const marker = L.circleMarker([node.lat, node.lng], {
                        radius: 2,
                        fillColor: '#3388ff',
                        color: '#3388ff',
                        weight: 1,
                        fillOpacity: 0.6
                    });

                    marker.bindPopup(`Node ID: ${node.id}<br>Lat: ${node.lat.toFixed(5)}<br>Lng: ${node.lng.toFixed(5)}`);

                    nodeMarkersLayer.addLayer(marker);
                });

                nodesVisible = true;
                btn.textContent = '🔴 Цэгүүдийг нуух';
                btn.classList.add('active');

                alert(`${nodes.length} цэг харуулав. Газрын зураг дээр цэг дээр дарж дэлгэрэнгүй мэдээлэл авна уу.`);
            } else {
                alert('Цэгүүд ачаалж чадсангүй');
            }
        } catch (error) {
            console.error('Node-ууд ачаалахад алдаа:', error);
            alert('Алдаа гарлаа: ' + error.message);
        } finally {
            showLoading(false);
        }
    }
}

function onMapClick(e) {
    const lat = e.latlng.lat;
    const lng = e.latlng.lng;

    if (!startCoords) {
        setStartPoint(lat, lng);
    } else if (!endCoords) {
        setEndPoint(lat, lng);
    } else {
        resetPoints();
        setStartPoint(lat, lng);
    }
}

function setStartPoint(lat, lng) {
    startCoords = { lat, lng };

    if (startMarker) {
        map.removeLayer(startMarker);
    }

    startMarker = L.circleMarker([lat, lng], {
        radius: 10,
        fillColor: '#28a745',
        color: 'white',
        weight: 3,
        fillOpacity: 1
    }).addTo(map);

    startMarker.bindPopup('🟢 Эхлэх цэг').openPopup();
}

function setEndPoint(lat, lng) {
    endCoords = { lat, lng };

    if (endMarker) {
        map.removeLayer(endMarker);
    }

    endMarker = L.circleMarker([lat, lng], {
        radius: 10,
        fillColor: '#dc3545',
        color: 'white',
        weight: 3,
        fillOpacity: 1
    }).addTo(map);

    endMarker.bindPopup('🔴 Дуусах цэг').openPopup();

    if (selectedAlgorithm) {
        findPath(selectedAlgorithm);
    }
}

function selectAlgorithm(algorithm) {
    selectedAlgorithm = algorithm;

    document.querySelectorAll('.algo-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    document.getElementById(`btn-${algorithm}`).classList.add('active');

    if (startCoords && endCoords) {
        findPath(algorithm);
    }
}

async function findPath(algorithm) {
    if (!startCoords || !endCoords) {
        alert('Эхлэх болон дуусах цэгээ сонгоно уу!');
        return;
    }

    showLoading(true);
    clearPaths();

    try {
        let result;

        switch(algorithm) {
            case 'bfs':
                result = await API.findPathBFS(
                    startCoords.lat, startCoords.lng,
                    endCoords.lat, endCoords.lng
                );
                break;
            case 'dfs':
                result = await API.findPathDFS(
                    startCoords.lat, startCoords.lng,
                    endCoords.lat, endCoords.lng
                );
                break;
            case 'dijkstra':
                result = await API.findPathDijkstra(
                    startCoords.lat, startCoords.lng,
                    endCoords.lat, endCoords.lng
                );
                break;
        }

        if (result.success && result.path) {
            drawPath(result.path, getAlgorithmColor(algorithm));
            displayResults([result]);
        } else {
            alert('Зам олдсонгүй: ' + result.message);
        }

    } catch (error) {
        alert('Алдаа гарлаа: ' + error.message);
    } finally {
        showLoading(false);
    }
}

async function compareAllAlgorithms() {
    if (!startCoords || !endCoords) {
        alert('Эхлэх болон дуусах цэгээ сонгоно уу!');
        return;
    }

    showLoading(true);
    clearPaths();

    try {
        const results = await API.compareAlgorithms(
            startCoords.lat, startCoords.lng,
            endCoords.lat, endCoords.lng
        );

        const resultArray = [
            { ...results.bfs, color: getAlgorithmColor('bfs') },
            { ...results.dfs, color: getAlgorithmColor('dfs') },
            { ...results.dijkstra, color: getAlgorithmColor('dijkstra') }
        ];

        resultArray.forEach(result => {
            if (result.success && result.path) {
                drawPath(result.path, result.color);
            }
        });

        displayResults(resultArray);

    } catch (error) {
        alert('Алдаа гарлаа: ' + error.message);
    } finally {
        showLoading(false);
    }
}

function drawPath(path, color) {
    const latLngs = path.map(point => [point.lat, point.lng]);

    const polyline = L.polyline(latLngs, {
        color: color,
        weight: 5,
        opacity: 0.7
    }).addTo(map);

    pathLayers.push(polyline);

    map.fitBounds(polyline.getBounds(), { padding: [50, 50] });
}

function displayResults(results) {
    const resultsPanel = document.getElementById('results');
    const resultsContent = document.getElementById('results-content');

    resultsContent.innerHTML = '';

    results.forEach(result => {
        const card = document.createElement('div');
        card.className = 'result-card';

        const statusClass = result.success ? 'status-success' : 'status-error';
        const statusText = result.success ? '✓ Амжилттай' : '✗ Олдсонгүй';

        card.innerHTML = `
            <div class="result-header">
                <span class="result-algo">${result.algorithm}</span>
                <span class="result-status ${statusClass}">${statusText}</span>
            </div>
            ${result.success ? `
                <div class="result-stats">
                    <div class="stat">
                        <span class="stat-value">${result.pathLength}</span>
                        <span class="stat-label">Цэгүүд</span>
                    </div>
                    <div class="stat">
                        <span class="stat-value">${result.totalDistance.toFixed(2)}</span>
                        <span class="stat-label">км</span>
                    </div>
                    <div class="stat">
                        <span class="stat-value">${result.executionTime.toFixed(2)}</span>
                        <span class="stat-label">мс</span>
                    </div>
                </div>
            ` : `<p>${result.message}</p>`}
        `;

        resultsContent.appendChild(card);
    });

    resultsPanel.classList.remove('hidden');
}

function getAlgorithmColor(algorithm) {
    const colors = {
        'bfs': '#007bff',
        'dfs': '#28a745',
        'dijkstra': '#dc3545'
    };
    return colors[algorithm] || '#6c757d';
}

function clearPaths() {
    pathLayers.forEach(layer => map.removeLayer(layer));
    pathLayers = [];
}

function resetPoints() {
    if (startMarker) map.removeLayer(startMarker);
    if (endMarker) map.removeLayer(endMarker);

    startMarker = null;
    endMarker = null;
    startCoords = null;
    endCoords = null;

    clearPaths();
}

function clearAll() {
    resetPoints();

    document.getElementById('results').classList.add('hidden');

    selectedAlgorithm = null;
    document.querySelectorAll('.algo-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    if (nodesVisible) {
        nodeMarkersLayer.clearLayers();
        nodesVisible = false;
        const btn = document.getElementById('btn-show-nodes');
        btn.textContent = '📍 Бүх цэгүүд';
        btn.classList.remove('active');
    }

    map.setView(UB_CENTER, UB_ZOOM);
}

function showLoading(show) {
    const loading = document.getElementById('loading');
    if (show) {
        loading.classList.remove('hidden');
    } else {
        loading.classList.add('hidden');
    }
}

async function loadGraphStats() {
    const stats = await API.getGraphStats();
    if (stats) {
        console.log('График статистик:', stats);
    }
}
