/**
 * Backend API-тэй харилцах модуль
 */

const API_BASE_URL = 'http://localhost:8080/api';

const API = {
    /**
     * BFS алгоритм ашиглан зам олох
     */
    async findPathBFS(startLat, startLng, endLat, endLng) {
        return await this.request('/path/bfs', {
            startLat, startLng, endLat, endLng
        });
    },

    /**
     * DFS алгоритм ашиглан зам олох
     */
    async findPathDFS(startLat, startLng, endLat, endLng) {
        return await this.request('/path/dfs', {
            startLat, startLng, endLat, endLng
        });
    },

    /**
     * Dijkstra алгоритм ашиглан зам олох
     */
    async findPathDijkstra(startLat, startLng, endLat, endLng) {
        return await this.request('/path/dijkstra', {
            startLat, startLng, endLat, endLng
        });
    },

    /**
     * Гурван алгоритмыг харьцуулах
     */
    async compareAlgorithms(startLat, startLng, endLat, endLng) {
        return await this.request('/path/compare', {
            startLat, startLng, endLat, endLng
        });
    },

    /**
     * График статистик авах
     */
    async getGraphStats() {
        try {
            const response = await fetch(`${API_BASE_URL}/graph/stats`);
            return await response.json();
        } catch (error) {
            console.error('График статистик авахад алдаа:', error);
            return null;
        }
    },

    /**
     * ШИНЭ: Бүх node-уудыг авах
     */
    async getAllNodes() {
        try {
            const response = await fetch(`${API_BASE_URL}/graph/nodes`);
            return await response.json();
        } catch (error) {
            console.error('Node-ууд авахад алдаа:', error);
            return null;
        }
    },

    /**
     * Ерөнхий хүсэлт илгээх функц
     */
    async request(endpoint, data) {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('API хүсэлтэд алдаа гарлаа:', error);
            return {
                success: false,
                message: 'Серверт холбогдож чадсангүй: ' + error.message
            };
        }
    }
};
