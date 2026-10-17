import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

api.interceptors.request.use((cfg) => {
  const token = localStorage.getItem('token');
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  return cfg;
});

export const errMsg = (e) =>
  e.response?.data?.errors
    ? Object.values(e.response.data.errors).join(', ')
    : e.response?.data?.message || 'Request failed. Please try again.';

export const money = (n) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(n);

export default api;
