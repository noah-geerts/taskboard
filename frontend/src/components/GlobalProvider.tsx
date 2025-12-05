import {
  createContext,
  useMemo,
  useState,
  useContext,
  type ReactNode,
  type Dispatch,
  type SetStateAction,
} from 'react'
import { useGetAllTasks } from '../api/taskService'
import type { Task } from '../domain/Task'

type TasksContextValue = { visibleTasks: Task[] }
type FilterContextValue = {
  query: string
  setQuery: Dispatch<SetStateAction<string>>
  selectedPriorities: number[]
  setSelectedPriorities: Dispatch<SetStateAction<number[]>>
}

export const TasksContext = createContext<TasksContextValue | undefined>(
  undefined
)
export const FilterContext = createContext<FilterContextValue | undefined>(
  undefined
)

export default function GlobalProvider({ children }: { children: ReactNode }) {
  const { data: tasks } = useGetAllTasks()
  const [query, setQuery] = useState('')
  const [selectedPriorities, setSelectedPriorities] = useState([0, 1, 2])

  const visibleTasks = useMemo(
    () =>
      tasks
        ? tasks.filter((task: Task) => {
            return (
              (task.title.includes(query) ||
                task.description.includes(query)) &&
              selectedPriorities.includes(task.priority)
            )
          })
        : [],
    [tasks, query, selectedPriorities]
  )

  return (
    <TasksContext.Provider value={{ visibleTasks }}>
      <FilterContext.Provider
        value={{ query, setQuery, selectedPriorities, setSelectedPriorities }}
      >
        {children}
      </FilterContext.Provider>
    </TasksContext.Provider>
  )
}

export function useTasksContext(): TasksContextValue {
  const ctx = useContext(TasksContext)
  if (!ctx)
    throw new Error('useTasksContext must be used within GlobalProvider')
  return ctx
}

export function useFilterContext(): FilterContextValue {
  const ctx = useContext(FilterContext)
  if (!ctx)
    throw new Error('useFilterContext must be used within GlobalProvider')
  return ctx
}
