import axios from 'axios';

// Cliente preparado para futuras consultas; no se hacen peticiones en Prompt 01.
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
});

export default api;
