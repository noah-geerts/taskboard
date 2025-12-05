import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, redirect } from 'react-router'
import { RouterProvider } from 'react-router/dom'
import Login from './pages/Login'
import Signup from './pages/Signup'
import App from './pages/App'
import './base.css'
import './index.css'

export async function authLoader() {
  const token = localStorage.getItem('jwt')
  if (!token) return redirect('/auth/login')
  return null
}

const router = createBrowserRouter([
  {
    path: '/auth',
    children: [
      {
        path: 'login',
        element: <Login />,
      },
      {
        path: 'signup',
        element: <Signup />,
      },
    ],
  },
  {
    path: '/',
    element: <App />,
    loader: authLoader,
  },
])

const root = createRoot(document.getElementById('root'))
root.render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
)
