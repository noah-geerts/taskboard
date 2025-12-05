import GlobalProvider from '../components/GlobalProvider'
import Header from '../components/Header'
import TaskColumn from '../components/TaskColumn'

import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

const queryClient = new QueryClient()

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <GlobalProvider>
        <div className="w-full h-full flex flex-col">
          <Header />
          <main className="flex-1 flex flex-row gap-4 p-4">
            <TaskColumn status={0} />
            <TaskColumn status={1} />
            <TaskColumn status={2} />
          </main>
        </div>
      </GlobalProvider>
    </QueryClientProvider>
  )
}
