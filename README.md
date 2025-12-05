# Welcome to Taskboard

## Quick Start

### Backend

1. Ensure you have maven and java installed
2. run `cd backend`
3. run `docker-compose` up to spin up a postgre Docker container
4. run `mvn spring-boot:run` to start the backend dev server

### Frontend

1. Ensure you have node and npm installed
2. run `cd frontend`
3. run `npm install` to install dependencies
4. run `npm run dev` to spin up the vite dev server

## Environment Variables

### Frontend

- VITE_API_URL: must contain the URL of the backend development server
