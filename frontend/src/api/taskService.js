import api from './client'
import { useQuery, useQueryClient, useMutation } from '@tanstack/react-query'

export function useGetAllTasks() {
  return useQuery({
    queryKey: ['tasks'],
    queryFn: async () => {
      const { data } = await api.get('/tasks')
      console.log(data)
      return data
    },
  })
}

export function useCreateTask() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (task) => {
      const { tid, ...taskNoId } = task
      const { data } = await api.post('/tasks', taskNoId)
      console.log(data)
      return data
    },
    // optimistic update
    onMutate: async (newTask) => {
      await queryClient.cancelQueries({ queryKey: ['tasks'] })

      const previousTasks = queryClient.getQueryData(['tasks'])

      // optimistically update cache
      queryClient.setQueryData(['tasks'], (old = []) => [
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
    mutationFn: async (task) => {
      const { tid, ...taskNoId } = task
      const { data } = await api.patch(`/tasks?tid=${tid}`, taskNoId)
      console.log(data)
      return data
    },
    // optimistic update
    onMutate: async (updatedTask) => {
      await queryClient.cancelQueries({ queryKey: ['tasks'] })

      const previousTasks = queryClient.getQueryData(['tasks'])

      // optimistically update cache
      queryClient.setQueryData(['tasks'], (old = []) =>
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
    mutationFn: async (task) => {
      const { data } = await api.delete(`/tasks?tid=${task.tid}`)
      console.log(data)
      return data
    },
    // optimistic update
    onMutate: async (deletedTask) => {
      await queryClient.cancelQueries({ queryKey: ['tasks'] })

      const previousTasks = queryClient.getQueryData(['tasks'])

      // optimistically update cache
      queryClient.setQueryData(['tasks'], (old = []) =>
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
