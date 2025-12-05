import { useTasksContext } from './GlobalProvider.js'
import { statusMap } from '../utils/utils.js'
import Task from './Task.js'
import { useCreateTask } from '../api/taskService.js'
import type { Status } from '../domain/Task.js'

export default function TaskColumn({ status }: { status: Status }) {
  const { visibleTasks } = useTasksContext()
  const createTask = useCreateTask()
  const thisStatusTasks = visibleTasks.filter((task) => task.status === status)

  return (
    <div className="flex-1 flex flex-col p-2 bg-[#f8f8f8] border-2 border-gray-100 rounded-md">
      <div className="flex flex-row py-2 justify-between px-1">
        <h1 className="text-lg font-bold">{statusMap[status]}</h1>
        <button
          className="h-6 text-xl"
          onClick={() =>
            createTask.mutate({
              tid: -1,
              title: 'New Task',
              description: 'description',
              status: status,
              priority: 1,
            })
          }
        >
          +
        </button>
      </div>
      <div className="flex flex-col flex-1 gap-2">
        {thisStatusTasks.map((task) => (
          <Task key={task.tid} task={task}></Task>
        ))}
      </div>
    </div>
  )
}
