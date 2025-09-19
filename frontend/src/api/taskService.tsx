import type { Task } from '../domain/Task'
import type { TaskRequestDto } from '../domain/TaskRequestDto'
import type { TaskResponseDto } from '../domain/TaskResponseDto'
import api from './client'
import {
  useQuery,
  useQueryClient,
  useMutation,
  type UseQueryResult,
  type UseMutationResult,
} from '@tanstack/react-query'

export function useGetAllTasks(): UseQueryResult<Task[], Error> {
  return useQuery({
    queryKey: ['tasks'],
    queryFn: async () => {
      const { data }: { data: Task[] } = await api.get('/tasks')
      console.log(data)
      return data
    },
  })
}

export function useCreateTask() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (newTask: Task) => {
      const { tid, ...taskNoId } = newTask
      const { data } = await api.post('/tasks', taskNoId)
      console.log(data)
      return data
    },
    // optimistic update
    onMutate: async (newTask: Task) => {
      await queryClient.cancelQueries({ queryKey: ['tasks'] })

      const previousTasks = queryClient.getQueryData(['tasks'])

      // optimistically update cache
      queryClient.setQueryData(['tasks'], (old: Task[] = []) => [
        ...old,
        { ...newTask, tid: crypto.randomUUID() },
      ])

      return { previousTasks }
    },
    onError: (_err, _newTask, context) => {
      // rollback
      if (context?.previousTasks) {
        queryClient.setQueryData(['tasks'], context.previousTasks)
      }
    },
    onSettled: () => {
      // always refetch to get server state
      queryClient.invalidateQueries({ queryKey: ['tasks'] })
    },
  })
}

export function useUpdateTask() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (updatedTask: Task) => {
      const { tid, ...taskNoId } = updatedTask
      const { data } = await api.patch(`/tasks?tid=${tid}`, taskNoId)
      console.log(data)
      return data
    },
    // optimistic update
    onMutate: async (updatedTask: Task) => {
      await queryClient.cancelQueries({ queryKey: ['tasks'] })

      const previousTasks = queryClient.getQueryData(['tasks'])

      // optimistically update cache
      queryClient.setQueryData(['tasks'], (old: Task[] = []) =>
        old.map((task) =>
          task.tid === updatedTask.tid ? { ...updatedTask } : task
        )
      )

      return { previousTasks }
    },
    onError: (_err, _newTask, context) => {
      // rollback
      if (context?.previousTasks) {
        queryClient.setQueryData(['tasks'], context.previousTasks)
      }
    },
    onSettled: () => {
      // always refetch to get server state
      queryClient.invalidateQueries({ queryKey: ['tasks'] })
    },
  })
}

export function useDeleteTask() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (deletedTask: Task) => {
      const { data } = await api.delete(`/tasks?tid=${deletedTask.tid}`)
      console.log(data)
      return data
    },
    // optimistic update
    onMutate: async (deletedTask: Task) => {
      await queryClient.cancelQueries({ queryKey: ['tasks'] })

      const previousTasks = queryClient.getQueryData(['tasks'])

      // optimistically update cache
      queryClient.setQueryData(['tasks'], (old: Task[] = []) =>
        old.filter((task) => task.tid !== deletedTask.tid)
      )

      return { previousTasks }
    },
    onError: (_err, _newTask, context) => {
      // rollback
      if (context?.previousTasks) {
        queryClient.setQueryData(['tasks'], context.previousTasks)
      }
    },
    onSettled: () => {
      // always refetch to get server state
      queryClient.invalidateQueries({ queryKey: ['tasks'] })
    },
  })
}
