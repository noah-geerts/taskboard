import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  withCredentials: true, // so cookies (refreshToken) are included,
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${localStorage.getItem('jwt')}`,
  },
})

// Interceptor to add JWT to all requests
api.interceptors.request.use(
  (request) => {
    const token = localStorage.getItem('jwt')
    if (token) {
      request.headers.Authorization = `Bearer ${token}`
    } else {
      console.log('jwt not found in local storage')
    }
    return request
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => response, // Directly return successful responses.
  async (error) => {
    const originalRequest = error.config
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true // Mark the request as retried to avoid infinite loops.

      try {
        // Make a request to your auth server to refresh the token.
        const response = await axios.post(
          import.meta.env.VITE_API_URL + '/auth/refresh',
          {},
          { withCredentials: true }
        )
        const { jwt } = response.data

        // Store the new jwt
        localStorage.setItem('jwt', jwt)

        // Update the authorization header with the new access token.
        api.defaults.headers.common['Authorization'] = `Bearer ${jwt}`
        return api(originalRequest) // Retry the original request with the new access token.
      } catch (refreshError) {
        // Handle refresh token errors by clearing stored tokens and redirecting to the login page.
        console.error('Token refresh failed:', refreshError)
        localStorage.removeItem('jwt')
        window.location.href = '/auth/login'
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error) // For all other errors, return the error as is.
  }
)

export default api
