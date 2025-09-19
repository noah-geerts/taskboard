import { createContext, useMemo, useState } from 'react'
import { useGetAllTasks } from './api/taskService'

export const TasksContext = createContext({})
export const FilterContext = createContext('')

export default function GlobalProvider({ children }) {
  const { data: tasks, error, isLoading, isError } = useGetAllTasks()
  const [query, setQuery] = useState('')
  const [selectedPriorities, setSelectedPriorities] = useState([0, 1, 2])

  const visibleTasks = useMemo(
    () =>
      tasks
        ? tasks.filter((task) => {
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
    <TasksContext value={{ visibleTasks }}>
      <FilterContext
        value={{ query, setQuery, selectedPriorities, setSelectedPriorities }}
      >
        {children}
      </FilterContext>
    </TasksContext>
  )
}
