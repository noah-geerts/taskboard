import { useRef, useEffect, useMemo, useState } from 'react'
import { useDeleteTask, useUpdateTask } from '../api/taskService'
import { priorityColors } from '../types/Types'
import { debounce } from '../utils/debounce'

function mod(n, m) {
  return ((n % m) + m) % m
}

export default function Task({ task }) {
  const updateTask = useUpdateTask()
  const deleteTask = useDeleteTask()
  const descriptionRef = useRef(null)

  const [title, setTitle] = useState(task.title)
  const [description, setDescription] = useState(task.description)

  const debouncedUpdateTitle = useMemo(
    () =>
      debounce((value) => {
        updateTask.mutate({ ...task, title: value })
      }, 500),
    [task] // dependencies
  )

  const debouncedUpdateDescription = useMemo(
    () =>
      debounce((value) => {
        updateTask.mutate({ ...task, description: value })
      }, 500),
    [task] // dependencies
  )

  useEffect(() => {
    if (descriptionRef.current) {
      descriptionRef.current.style.height = 'auto'
      descriptionRef.current.style.height =
        descriptionRef.current.scrollHeight + 'px'
    }
  }, [task])

  let bgColor = priorityColors[task.priority]

  return (
    <div
      className={`flex flex-col bg-white gap-2 border-2 border-gray-100 p-3 rounded-md`}
    >
      <div className="flex flex-row justify-between items-center gap-2">
        <div className={`rounded-full ${bgColor} w-3 h-3`}></div>
        <input
          className="text-md font-bold bg-transparent w-2/3 focus:outline-none focus:ring-0"
          value={title}
          onChange={(e) => {
            debouncedUpdateTitle(e.target.value)
            setTitle(e.target.value)
          }}
        ></input>
        <div className="flex flex-row gap-2">
          <select
            value={task.priority}
            onChange={(e) =>
              updateTask.mutate({
                ...task,
                priority: e.target.value,
              })
            }
          >
            <option value={2}>urgent</option>
            <option value={1}>standard</option>
            <option value={0}>secondary</option>
          </select>
          <button className="text-lg" onClick={() => deleteTask.mutate(task)}>
            x
          </button>
        </div>
      </div>

      <div className="flex flex-row justify-between gap-6">
        <button
          className="text-lg"
          onClick={(e) =>
            updateTask.mutate({
              ...task,
              status: mod(task.status - 1, 3),
            })
          }
        >
          {'<'}
        </button>
        <textarea
          ref={descriptionRef}
          className="bg-transparent flex-1 text-gray-600 focus:outline-none focus:ring-0 resize-none"
          value={description}
          onChange={(e) => {
            debouncedUpdateDescription(e.target.value)
            setDescription(e.target.value)
          }}
        ></textarea>
        <button
          className="text-lg"
          onClick={(e) =>
            updateTask.mutate({
              ...task,
              status: mod(task.status + 1, 3),
            })
          }
        >
          {'>'}
        </button>
      </div>
    </div>
  )
}
