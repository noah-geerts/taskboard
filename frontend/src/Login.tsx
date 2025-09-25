import { useState } from 'react'
import axios from 'axios'
import type { AuthResponseDto } from './domain/AuthResponseDto'
import { useNavigate, NavLink } from 'react-router'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const navigate = useNavigate()

  const handleChangeEmail: React.ChangeEventHandler<HTMLInputElement> = (e) => {
    setEmail(e.target.value)
  }

  const handleChangePassword: React.ChangeEventHandler<HTMLInputElement> = (
    e
  ) => {
    setPassword(e.target.value)
  }

  const handleSubmit = (e?: React.FormEvent) => {
    e?.preventDefault()
    axios
      .post(
        import.meta.env.VITE_API_URL + '/auth/login',
        {
          email: email,
          password: password,
        },
        { withCredentials: true }
      )
      .then((response) => {
        const json: AuthResponseDto = response.data
        console.log(JSON.stringify(json, null, 2))
        localStorage.setItem('jwt', json.jwt)
        navigate('/')
      })
      .catch((error) => {
        console.error('Error:', error)
      })
  }

  return (
    <div className="w-full h-full flex flex-col justify-center items-center bg-gray-50">
      <div className="bg-white rounded-lg shadow-md p-8 w-full max-w-md">
        <h1 className="text-2xl font-bold text-center mb-6 text-gray-800">
          Log in
        </h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div>
            <label
              htmlFor="email"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Email
            </label>
            <input
              id="email"
              value={email}
              onChange={handleChangeEmail}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Enter your email"
              type="email"
              required
            />
          </div>

          <div>
            <label
              htmlFor="password"
              className="block text-sm font-medium text-gray-700 mb-1"
            >
              Password
            </label>
            <input
              id="password"
              value={password}
              onChange={handleChangePassword}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Enter your password"
              type="password"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-md transition-colors duration-200 mt-2"
          >
            Log In
          </button>
        </form>

        <div className="text-center mt-6">
          <NavLink
            to="/auth/signup"
            className="text-blue-500 hover:text-blue-600 text-sm transition-colors duration-200"
          >
            Don't have an account? Sign up
          </NavLink>
        </div>
      </div>
    </div>
  )
}
